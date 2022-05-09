package com.github.longkerdandy.burger.backend.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Role-based access control.
 */
@SuppressWarnings("unused")
public enum Role implements GrantedAuthority {

  ADMIN,
  USER;

  @Override
  public String getAuthority() {
    return this.name();
  }
}
