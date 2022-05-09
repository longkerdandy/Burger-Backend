package com.github.longkerdandy.burger.backend.repository;

import static java.time.Instant.now;
import static org.springframework.data.geo.Metrics.KILOMETERS;

import com.github.longkerdandy.burger.backend.model.Restaurant;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for restaurant related operations.
 */
@Slf4j
@Repository
public class RestaurantRepository {

  // MongoDB Collections
  public static final String COLLECTION_RESTAURANTS = "restaurants";
  // MongoDB Fields
  public static final String FIELD_ID = "_id";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_LOGO = "logo";
  public static final String FIELD_LOCATION = "location";
  public static final String FIELD_ADDRESS = "address";
  public static final String FIELD_IMAGES = "images";
  public static final String FIELD_OPENING = "opening";
  public static final String FIELD_MENU = "menu";
  public static final String FIELD_RATING = "rating";
  public static final String FIELD_DISTANCE = "distance";
  public static final String FIELD_UPDATED_AT = "updatedAt";

  private final MongoTemplate mongo;              // Spring MongoTemplate

  @Autowired
  public RestaurantRepository(MongoTemplate mongo) {
    this.mongo = mongo;
  }

  /**
   * Whether {@link Restaurant} already exist. This will check id.
   *
   * @param id {@link Restaurant} id
   * @return True if exists
   */
  public boolean isRestaurantExists(ObjectId id) {
    return this.mongo.exists(new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }

  /**
   * Insert new {@link Restaurant} to database.
   *
   * @param restaurant {@link Restaurant} to be added
   * @return Inserted {@link Restaurant}
   */
  public Restaurant insertRestaurant(Restaurant restaurant) {
    restaurant.setUpdatedAt(now());
    return this.mongo.insert(restaurant, COLLECTION_RESTAURANTS);
  }

  /**
   * Find the first record of {@link Restaurant}.
   *
   * @return {@link Restaurant}
   */
  public Restaurant findRandomRestaurant() {
    return this.mongo.findOne(new Query(), Restaurant.class, COLLECTION_RESTAURANTS);
  }

  /**
   * Find the {@link Restaurant} with specific id.
   *
   * @param id value
   * @return {@link Restaurant}
   */
  public Restaurant findRestaurantById(ObjectId id) {
    return this.mongo.findOne(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }

  /**
   * Find the {@link Restaurant}s within given range, sort by distance.
   *
   * @param point       Target coordinate
   * @param maxDistance Max distance in kilometers
   * @param skip        Skipped records
   * @param limit       Limit result count
   * @return List of {@link Restaurant}
   */
  public List<Restaurant> findRestaurantsByLocation(GeoJsonPoint point, double maxDistance,
      long skip, long limit) {
    GeoNearOperation geoNearOp = Aggregation.geoNear(
        NearQuery.near(point, KILOMETERS).maxDistance(maxDistance).spherical(true), FIELD_DISTANCE);
    ProjectionOperation projectOp =
        Aggregation.project(FIELD_NAME, FIELD_LOGO, FIELD_RATING, FIELD_DISTANCE);
    SkipOperation skipOp = Aggregation.skip(skip);
    LimitOperation limitOp = Aggregation.limit(limit);
    Aggregation aggregation = Aggregation.newAggregation(geoNearOp, projectOp, skipOp, limitOp);
    return this.mongo.aggregate(aggregation, COLLECTION_RESTAURANTS, Restaurant.class)
        .getMappedResults();
  }

  /**
   * Update the {@link Restaurant} with specific id. NOTE: {@link #FIELD_RATING} is excluded.
   *
   * @param restaurant {@link Restaurant}
   * @return {@link UpdateResult}
   */
  public UpdateResult updateRestaurant(Restaurant restaurant) {
    return this.mongo.updateFirst(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(restaurant.getId())),
        new Update()
            .set(FIELD_NAME, restaurant.getName())
            .set(FIELD_LOGO, restaurant.getLogo())
            .set(FIELD_LOCATION, restaurant.getLocation())
            .set(FIELD_ADDRESS, restaurant.getAddress())
            .set(FIELD_IMAGES, restaurant.getImages())
            .set(FIELD_OPENING, restaurant.getOpening())
            .set(FIELD_MENU, restaurant.getMenu())
            .set(FIELD_UPDATED_AT, now()),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }

  /**
   * Delete all the {@link Restaurant}s which name doesn't contain the keyword.
   *
   * @param keyword Keyword
   * @return {@link DeleteResult}
   */
  public DeleteResult deleteRestaurantsNonContains(String keyword) {
    return this.mongo.remove(
        new Query().addCriteria(Criteria.where(FIELD_NAME).not().regex(keyword, "i")),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }

  /**
   * Delete the {@link Restaurant} with specific id.
   *
   * @param id value
   * @return {@link DeleteResult}
   */
  public DeleteResult deleteRestaurantById(ObjectId id) {
    return this.mongo.remove(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }
}
