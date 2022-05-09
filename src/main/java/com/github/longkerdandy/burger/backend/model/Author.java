package com.github.longkerdandy.burger.backend.model;

import java.net.URL;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Author is a brief version of {@link User}, used in {@link Review}. The {@link User}'s id is not
 * saved here, because username is also unique.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Author {

  // Username
  private String username;
  // Image url
  private URL avatar;
  // Nickname, which actually displayed on Web or App
  private String nickname;
}
