package com.github.longkerdandy.burger.backend.model;

import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represent an end user.
 *
 * <p>{@link UserDetails} contains necessary information (such as: username, password, authorities)
 * to build an {@link Authentication} object.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class User implements UserDetails {

  // Id
  @Id
  private ObjectId id;
  // Username
  private String username;
  // Email
  private String email;
  // Mobile phone
  private String phone;
  // Encoded password
  private String password;
  // Image url
  private URL avatar;
  // Nickname, which actually displayed on Web or App
  private String nickname;
  // Personal profile
  private String profile;
  // Roles
  private List<Role> roles;
  // Updated timestamp
  private Instant updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
