package com.github.longkerdandy.burger.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP update result response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UpdateResponse {

  // Count of matched records
  private long matched;
  // Count of modified records
  private long modified;
}
