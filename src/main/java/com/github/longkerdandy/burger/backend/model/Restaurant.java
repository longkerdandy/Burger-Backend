package com.github.longkerdandy.burger.backend.model;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;


/**
 * Represent a restaurant.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Restaurant {

  // Id
  @Id
  private ObjectId id;
  // Restaurant name
  private String name;
  // Logo
  private URL logo;
  // coordinate
  private GeoJsonPoint location;
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
  // READ ONLY! Distance to target coordinate in $geoNear query
  private Double distance;
  // Updated timestamp
  private Instant updatedAt;
}
