package com.github.longkerdandy.burger.backend.dto.request;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP login request payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LoginRequest {

  // Username
  @NotEmpty
  private String username;
  // Encoded password
  @NotEmpty
  private String password;
}
