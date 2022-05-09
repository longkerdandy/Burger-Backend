package com.github.longkerdandy.burger.backend.controller;

import com.github.longkerdandy.burger.backend.dto.request.RestaurantRequest;
import com.github.longkerdandy.burger.backend.dto.response.DeleteResponse;
import com.github.longkerdandy.burger.backend.dto.response.RestaurantResponse;
import com.github.longkerdandy.burger.backend.dto.response.UpdateResponse;
import com.github.longkerdandy.burger.backend.mapper.RestaurantMapper;
import com.github.longkerdandy.burger.backend.model.Restaurant;
import com.github.longkerdandy.burger.backend.repository.RestaurantRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restaurant controller.
 */
@Slf4j
@CrossOrigin
@RestController
@SuppressWarnings("unused")
public class RestaurantController {

  private final RestaurantMapper mapper;              // Restaurant Mapper
  private final RestaurantRepository repo;            // MongoDB

  /**
   * Constructor.
   */
  @Autowired
  public RestaurantController(RestaurantMapper mapper, RestaurantRepository repo) {
    this.mapper = mapper;
    this.repo = repo;
  }

  /**
   * Create a new {@link Restaurant}.
   *
   * @param request {@link RestaurantRequest}
   * @return {@link RestaurantResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PostMapping("/api/restaurants")
  public ResponseEntity<?> newRestaurant(@Valid @RequestBody RestaurantRequest request) {
    // Mapping from RestaurantRequest to Restaurant
    Restaurant restaurant = this.mapper.requestToRestaurant(request);
    // Insert new restaurant
    restaurant = this.repo.insertRestaurant(restaurant);
    // Response
    return ResponseEntity.ok(this.mapper.restaurantToResponse(restaurant));
  }

  /**
   * Get a random {@link Restaurant} record.
   *
   * @return {@link RestaurantResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @GetMapping("/api/restaurants/random")
  public ResponseEntity<?> getRandomRestaurant() {
    // Load random (actually first) restaurant record
    Restaurant restaurant = this.repo.findRandomRestaurant();
    // Response
    return ResponseEntity.ok(this.mapper.restaurantToResponse(restaurant));
  }

  /**
   * Update the {@link Restaurant} record with specific id.
   *
   * @return {@link UpdateResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @PutMapping("/api/restaurants/{id}")
  public ResponseEntity<?> updateRestaurant(@Valid @NotNull @PathVariable ObjectId id,
      @Valid @RequestBody RestaurantRequest request) {
    // Mapping from RestaurantRequest to Restaurant
    Restaurant restaurant = this.mapper.requestToRestaurant(request);
    restaurant.setId(id);
    // Try to update the restaurant record
    UpdateResult result = this.repo.updateRestaurant(restaurant);
    // Response
    return ResponseEntity.ok(
        new UpdateResponse()
            .setMatched(result.getMatchedCount())
            .setModified(result.getModifiedCount()));
  }

  /**
   * Delete the {@link Restaurant} record with specific id.
   *
   * @return {@link DeleteResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping("/api/restaurants/{id}")
  public ResponseEntity<?> deleteRestaurant(@Valid @NotNull @PathVariable ObjectId id) {
    // Try to delete the restaurant record
    DeleteResult result = this.repo.deleteRestaurantById(id);
    // Response
    return ResponseEntity.ok(new DeleteResponse().setDeleted(result.getDeletedCount()));
  }

  /**
   * Delete all {@link Restaurant} records which name doesn't contain burger.
   *
   * @return {@link DeleteResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping("/api/restaurants/purge")
  public ResponseEntity<?> deleteNonBurgerRestaurants() {
    // Try to delete all restaurants not related to burger
    DeleteResult result = this.repo.deleteRestaurantsNonContains("burger");
    // Response
    return ResponseEntity.ok(new DeleteResponse().setDeleted(result.getDeletedCount()));
  }

  /**
   * Get the {@link Restaurant} record with specific id.
   *
   * @return {@link RestaurantResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/restaurants/{id}")
  public ResponseEntity<?> getRestaurant(@Valid @NotNull @PathVariable ObjectId id) {
    // Load the restaurant record
    Restaurant restaurant = this.repo.findRestaurantById(id);
    // Response
    return ResponseEntity.ok(this.mapper.restaurantToResponse(restaurant));
  }

  /**
   * Find the {@link Restaurant} records within given range, sort by distance.
   *
   * @param latitude    latitude values are between -90 and 90, both inclusive
   * @param longitude   Valid longitude values are between -180 and 180, both inclusive
   * @param maxDistance Maximum distance in kilometers
   * @param skip        Records to be skipped
   * @param limit       Result size limit
   * @return List of {@link RestaurantResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/restaurants")
  public ResponseEntity<?> searchRestaurants(@Valid @NotNull @RequestParam double latitude,
      @Valid @NotNull @RequestParam double longitude,
      @Valid @NotNull @RequestParam double maxDistance,
      @RequestParam(defaultValue = "0") long skip,
      @RequestParam(defaultValue = "20") long limit) {
    // Create point, list the longitude first and then latitude
    GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
    // Search the restaurants based on the given coordinate
    List<Restaurant> restaurants =
        this.repo.findRestaurantsByLocation(point, maxDistance, skip, limit);
    // Response
    return ResponseEntity.ok(
        restaurants.stream()
            .map(this.mapper::restaurantToResponse)
            .collect(Collectors.toList()));
  }
}
