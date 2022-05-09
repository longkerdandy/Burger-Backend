package com.github.longkerdandy.burger.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP comment request payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CommentRequest {

  // Text content
  private String content;
}
