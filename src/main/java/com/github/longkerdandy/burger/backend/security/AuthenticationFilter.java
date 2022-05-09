package com.github.longkerdandy.burger.backend.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.github.longkerdandy.burger.backend.model.Role;
import com.github.longkerdandy.burger.backend.model.User;
import com.github.longkerdandy.burger.backend.util.JwtToolkits;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authentication request filter extends {@link OncePerRequestFilter} makes a single execution for
 * each request to our API. It provides a doFilterInternal() method that we will implement parsing &
 * validating JWT, loading User details (using UserDetailsService), checking {@link Authentication}
 * (using {@link UsernamePasswordAuthenticationToken}).
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl service; // UserDetailsService
  private final JwtToolkits jwtTool;            // JWT toolkit

  @Autowired
  public AuthenticationFilter(UserDetailsServiceImpl service, JwtToolkits jwtTool) {
    this.service = service;
    this.jwtTool = jwtTool;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // Parse JWT token
    String token = parseJwtToken(request);

    // If JWT token is valid
    if (token != null && this.jwtTool.validateJwtToken(token)) {
      // Read basic developer information from JWT token
      String username = this.jwtTool.getUsernameFromToken(token);
      // Load developer information from repository
      User user = (User) this.service.loadUserByUsername(username);
      // Build authentication, AuthenticationManager will use it to authenticate a sign in request
      List<Role> roles = user.getRoles();
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          user, null, roles);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      // Save authentication to the context
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Apply filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Parse JWT token in the HTTP header 'Authorization: Bearer token'.
   *
   * @param request {@link HttpServletRequest}
   * @return JWT token or null
   */
  protected String parseJwtToken(HttpServletRequest request) {
    // Authorization header
    String header = request.getHeader(AUTHORIZATION);

    // Return substring of JWT token part
    if (StringUtils.hasLength(header) && header.startsWith("Bearer ")) {
      return header.substring(7);
    }

    return null;
  }
}
