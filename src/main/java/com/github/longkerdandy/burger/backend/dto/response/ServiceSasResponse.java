package com.github.longkerdandy.burger.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HTTP service SAS response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ServiceSasResponse {

  // Service SAS
  private String sasToken;
  // Service SAS
  private String sasURL;
}
