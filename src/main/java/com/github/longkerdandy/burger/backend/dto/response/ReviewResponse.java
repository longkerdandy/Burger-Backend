package com.github.longkerdandy.burger.backend.dto.response;

import com.github.longkerdandy.burger.backend.model.Author;
import com.github.longkerdandy.burger.backend.model.Comment;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP review response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ReviewResponse {

  // Id
  private String id;
  // Restaurant id
  private String restaurantId;
  // Author information
  private Author author;
  // Review scores
  private int taste;
  private int texture;
  private int virtual;
  // Text content
  private String content;
  // Images
  private List<URL> images;
  // Comments
  private List<Comment> comments;
  // READ ONLY! This field will be calculated during MongoDB query
  private Long commentsCount;
  // Created at
  private Instant createdAt;
}
