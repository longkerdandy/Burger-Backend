package com.github.longkerdandy.burger.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP delete result response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DeleteResponse {

  // Count of deleted records
  private long deleted;
}
