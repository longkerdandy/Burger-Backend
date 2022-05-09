package com.github.longkerdandy.burger.backend.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represent a user's comment, used in {@link Review}. The comment can't be modified or deleted
 * after posted.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Comment {

  // Author information
  private Author author;
  // Text content
  private String content;
  // Created at
  private Instant createdAt;
}
