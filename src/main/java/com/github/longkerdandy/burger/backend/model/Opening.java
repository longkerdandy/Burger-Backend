package com.github.longkerdandy.burger.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represent opening times for restaurant.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Opening {

  // opening / closing time as UNIX time,
  // e.g. 32400 = 9 AM, 37800 = 10.30 AM
  // max value is 86399 = 11.59:59 PM
  private int workdayOpen;
  private int workdayClose;
  private int holidayOpen;
  private int holidayClose;
}
