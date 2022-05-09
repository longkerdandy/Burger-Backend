package com.github.longkerdandy.burger.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represent rating for {@link Restaurant}.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Rating {

  // Sum of taste scores
  private long tasteTotal;
  // Count of taste reviews
  private long tasteCount;
  // Sum of texture scores
  private long textureTotal;
  // Count of texture reviews
  private long textureCount;
  // Sum of virtual scores
  private long virtualTotal;
  // Count of virtual reviews
  private long virtualCount;
}
