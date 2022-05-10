package com.github.longkerdandy.burger.backend.dto.message;

import com.github.longkerdandy.burger.backend.model.Author;
import com.github.longkerdandy.burger.backend.model.Comment;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;

/**
 * EventHubs message payload.
 */
@Data
@Accessors(chain = true)
public class CommentMessage {

  // Review id
  private ObjectId reviewId;
  // Author information
  private Author author;
  // Text content
  private String content;
  // Created at
  private Instant createdAt;

  /**
   * Constructor.
   */
  public CommentMessage(ObjectId reviewId, Comment comment) {
    this.reviewId = reviewId;
    this.author = comment.getAuthor();
    this.content = comment.getContent();
    this.createdAt = comment.getCreatedAt();
  }
}
