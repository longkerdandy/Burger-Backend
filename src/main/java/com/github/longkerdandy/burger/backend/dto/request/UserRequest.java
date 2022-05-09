package com.github.longkerdandy.burger.backend.dto.request;

import java.net.URL;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP user request payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserRequest {

  // Username
  @NotEmpty
  private String username;
  // Email
  @Email
  private String email;
  // Mobile phone
  @NotEmpty
  private String phone;
  // Password
  private String password;
  // Image url
  private URL avatar;
  // Nickname
  private String nickname;
  // Personal profile
  private String profile;
}
