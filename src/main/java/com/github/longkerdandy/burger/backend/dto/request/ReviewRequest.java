package com.github.longkerdandy.burger.backend.dto.request;

import java.net.URL;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;

/**
 * HTTP review request payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ReviewRequest {

  // Restaurant id
  @NotNull
  private ObjectId restaurantId;
  // Review scores
  @Min(0)
  @Max(5)
  private int taste;
  @Min(0)
  @Max(5)
  private int texture;
  @Min(0)
  @Max(5)
  private int virtual;
  // Text content
  @NotEmpty
  private String content;
  // Images
  private List<URL> images;
}
