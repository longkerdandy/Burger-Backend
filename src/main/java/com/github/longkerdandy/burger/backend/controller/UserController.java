package com.github.longkerdandy.burger.backend.controller;

import static com.github.longkerdandy.burger.backend.model.Role.ADMIN;
import static com.github.longkerdandy.burger.backend.util.Anonymizers.anonymizeEmail;
import static com.github.longkerdandy.burger.backend.util.Anonymizers.anonymizePhone;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.github.longkerdandy.burger.backend.dto.request.LoginRequest;
import com.github.longkerdandy.burger.backend.dto.request.UserRequest;
import com.github.longkerdandy.burger.backend.dto.response.DeleteResponse;
import com.github.longkerdandy.burger.backend.dto.response.LoginResponse;
import com.github.longkerdandy.burger.backend.dto.response.UpdateResponse;
import com.github.longkerdandy.burger.backend.dto.response.UserResponse;
import com.github.longkerdandy.burger.backend.mapper.UserMapper;
import com.github.longkerdandy.burger.backend.model.Role;
import com.github.longkerdandy.burger.backend.model.User;
import com.github.longkerdandy.burger.backend.repository.UserRepository;
import com.github.longkerdandy.burger.backend.util.JwtToolkits;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication and user controller.
 */
@Slf4j
@CrossOrigin
@RestController
@SuppressWarnings("unused")
public class UserController {

  private final AuthenticationManager manager;  // Authentication manager
  private final JwtToolkits jwtTool;            // JWT toolkit
  private final UserMapper mapper;              // User mapper
  private final UserRepository repo;            // MongoDB

  /**
   * Constructor.
   */
  @Autowired
  public UserController(AuthenticationManager manager, JwtToolkits jwtTool, UserMapper mapper,
      UserRepository repo) {
    this.manager = manager;
    this.jwtTool = jwtTool;
    this.mapper = mapper;
    this.repo = repo;
  }

  /**
   * Login with username and password, return JWT token information.
   *
   * @param request {@link LoginRequest}
   * @return {@link LoginResponse}
   */
  @PostMapping("/api/auth/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    // TODO: SCRAM can be used to improve network security
    // Commonly referred to as SCRAM, is a protocol used to support password based authentication.
    // It is a revision to the previous CRAM protocol. Mutual authentication is established between
    // the client and server through sharing salt that was generated on the server and an
    // ic(iteration counter).
    // Authenticate request with AuthenticationManager
    Authentication authentication = this.manager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    // Save authenticate to the context
    SecurityContextHolder.getContext().setAuthentication(authentication);
    // Generate token
    String token = this.jwtTool.generateJwtToken(authentication);
    // Load user information from authentication
    User user = (User) authentication.getPrincipal();
    // Response
    return ResponseEntity.ok(
        new LoginResponse()
            .setAccessToken(token)
            .setAccessTokenExpiresAt(this.jwtTool.getExpiresAtFromToken(token).toInstant())
            .setUsername(user.getUsername())
            .setRoles(user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList())));
  }

  /**
   * Register {@link User} with necessary information, return JWT token information.
   *
   * @param request {@link UserRequest}
   * @return {@link LoginResponse}
   */
  @PostMapping("/api/auth/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
    // Mapping to user and assign USER role to registered user
    User user = this.mapper.requestToUser(request);
    user.setRoles(List.of(Role.USER));
    // To better handle exception, we manually check the user existence here
    if (this.repo.isUserExists(user)) {
      throw new ResponseStatusException(CONFLICT, "user already exists.");
    }
    // Insert new user information
    user = this.repo.insertUser(user);
    // Generate token
    String token = this.jwtTool.generateJwtToken(user);
    // Response, same as login
    return ResponseEntity.ok(
        new LoginResponse()
            .setAccessToken(token)
            .setAccessTokenExpiresAt(this.jwtTool.getExpiresAtFromToken(token).toInstant())
            .setUsername(user.getUsername())
            .setRoles(user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList())));
  }

  /**
   * Update {@link User} information.
   *
   * @param username of {@link User}
   * @param request  {@link UserRequest}
   * @return {@link UpdateResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @PutMapping("/api/users/{username}")
  public ResponseEntity<?> updateUser(@Valid @NotEmpty @PathVariable String username,
      @Valid @RequestBody UserRequest request) {
    // Load user information from context
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    List<Role> roles = user.getRoles();
    // If user is not ADMIN, he can only update his profile
    if (!user.getRoles().contains(ADMIN)) {
      if (!user.getUsername().equals(username)) {
        throw new ResponseStatusException(FORBIDDEN, "User can only edit his own profile.");
      }
    }
    // Mapping to user and assign roles
    user = this.mapper.requestToUser(request);
    user.setRoles(roles);
    // Try to update user record
    UpdateResult result = this.repo.updateUserByName(user);
    // Response
    return ResponseEntity.ok(
        new UpdateResponse()
            .setMatched(result.getMatchedCount())
            .setModified(result.getModifiedCount()));
  }

  /**
   * Get the {@link User} record with specific username.
   *
   * @param username of {@link User}
   * @return {@link UserResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/users/{username}")
  public ResponseEntity<?> getUser(@Valid @NotEmpty @PathVariable String username) {
    // To better handle exception, we manually check the user existence here
    if (!this.repo.isUserExists(username)) {
      throw new ResponseStatusException(NOT_FOUND, "user doesn't exist.");
    }
    // Load user information
    User user = this.repo.getUserByUsername(username);
    // Anonymize privacy data
    user.setPhone(anonymizePhone(user.getPhone()));
    user.setEmail(anonymizeEmail(user.getEmail()));
    // Response
    return ResponseEntity.ok(this.mapper.userToResponse(user));
  }

  /**
   * Delete the {@link User} record with specific username.
   *
   * @return {@link DeleteResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @DeleteMapping("/api/users/{username}")
  public ResponseEntity<?> deleteUser(@Valid @NotEmpty @PathVariable String username) {
    // Load user information from context
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    // If user is not ADMIN, he can only delete the review he posted
    if (!user.getRoles().contains(ADMIN)) {
      if (!user.getUsername().equals(username)) {
        throw new ResponseStatusException(FORBIDDEN, "Don't have permission to delete this user.");
      }
    }
    // Try to delete the restaurant record
    DeleteResult result = this.repo.deleteUserByName(username);
    // Response
    return ResponseEntity.ok(new DeleteResponse().setDeleted(result.getDeletedCount()));
  }
}
