package com.github.longkerdandy.burger.backend.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP login response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LoginResponse {

  // JWT token
  private String accessToken;
  // JWT token expiration
  private Instant accessTokenExpiresAt;
  // Username
  private String username;
  // Roles
  private List<String> roles;
}
