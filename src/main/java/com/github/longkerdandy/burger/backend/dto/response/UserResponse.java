package com.github.longkerdandy.burger.backend.dto.response;

import java.net.URL;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP user response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserResponse {

  // Username
  private String username;
  // Email
  private String email;
  // Mobile phone
  private String phone;
  // Image url
  private URL avatar;
  // Nickname
  private String nickname;
  // Personal profile
  private String profile;
}
