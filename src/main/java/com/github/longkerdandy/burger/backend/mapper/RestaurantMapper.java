package com.github.longkerdandy.burger.backend.mapper;

import com.github.longkerdandy.burger.backend.dto.request.RestaurantRequest;
import com.github.longkerdandy.burger.backend.dto.response.RestaurantResponse;
import com.github.longkerdandy.burger.backend.model.Restaurant;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * MapStruct mapper for {@link Restaurant}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantMapper {

  /**
   * {@link ObjectId} to String.
   */
  @Named("objIdToStr")
  static String objIdToStr(ObjectId id) {
    return id != null ? id.toHexString() : null;
  }

  /**
   * {@link GeoJsonPoint} to {@link Point}.
   */
  @Named("gjpToPoint")
  static Point gjpToPoint(GeoJsonPoint location) {
    return location != null ? new Point(location.getX(), location.getY()) : null;
  }

  /**
   * {@link Point} to {@link GeoJsonPoint}.
   */
  @Named("pointToGjp")
  static GeoJsonPoint pointToGjp(Point location) {
    return location != null ? new GeoJsonPoint(location) : null;
  }

  /**
   * Mapping from {@link RestaurantRequest} to {@link Restaurant}.
   *
   * @param request {@link RestaurantRequest}
   * @return Mapped {@link Restaurant}
   */
  @Mapping(source = "location", target = "location", qualifiedByName = "pointToGjp")
  Restaurant requestToRestaurant(RestaurantRequest request);

  /**
   * Mapping from {@link Restaurant} to {@link RestaurantResponse}.
   *
   * @param restaurant {@link Restaurant}
   * @return Mapped {@link RestaurantResponse}
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "objIdToStr")
  @Mapping(source = "location", target = "location", qualifiedByName = "gjpToPoint")
  RestaurantResponse restaurantToResponse(Restaurant restaurant);
}
