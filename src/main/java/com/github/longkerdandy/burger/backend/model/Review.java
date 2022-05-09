package com.github.longkerdandy.burger.backend.model;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Represent a user posted review for a restaurant.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Review {

  // Id
  @Id
  private ObjectId id;
  // Restaurant id
  private ObjectId restaurantId;
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
  // Updated timestamp
  private Instant updatedAt;
}
