package com.github.longkerdandy.burger.backend.dto.response;

import com.github.longkerdandy.burger.backend.model.MenuItem;
import com.github.longkerdandy.burger.backend.model.Opening;
import com.github.longkerdandy.burger.backend.model.Rating;
import java.net.URL;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.geo.Point;

/**
 * HTTP restaurant response payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RestaurantResponse {

  // Id
  private String id;
  // Restaurant name
  private String name;
  // Logo
  private URL logo;
  // Restaurant coordinates
  private Point location;
  // Address
  private String address;
  // Background images
  private List<URL> images;
  // Opening times
  private Opening opening;
  // Menu
  private List<MenuItem> menu;
  // Rating
  private Rating rating;
  // Distance to target coordinate
  private Double distance;
}
