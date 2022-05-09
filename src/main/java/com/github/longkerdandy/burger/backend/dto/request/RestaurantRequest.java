package com.github.longkerdandy.burger.backend.dto.request;

import com.github.longkerdandy.burger.backend.model.MenuItem;
import com.github.longkerdandy.burger.backend.model.Opening;
import java.net.URL;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.geo.Point;

/**
 * HTTP restaurant request payload.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RestaurantRequest {

  // Restaurant name
  @NotEmpty
  private String name;
  // Logo
  @NotNull
  private URL logo;
  // Restaurant coordinates
  @NotNull
  private Point location;
  // Address
  @NotEmpty
  private String address;
  // Background images
  private List<URL> images;
  // Opening times
  private Opening opening;
  // Menu
  private List<MenuItem> menu;
}
