package com.github.longkerdandy.burger.backend.config;

import static com.github.longkerdandy.burger.backend.repository.RestaurantRepository.COLLECTION_RESTAURANTS;
import static com.github.longkerdandy.burger.backend.repository.RestaurantRepository.FIELD_LOCATION;
import static com.github.longkerdandy.burger.backend.repository.ReviewRepository.COLLECTION_REVIEWS;
import static com.github.longkerdandy.burger.backend.repository.UserRepository.COLLECTION_USERS;
import static com.github.longkerdandy.burger.backend.repository.UserRepository.FIELD_EMAIL;
import static com.github.longkerdandy.burger.backend.repository.UserRepository.FIELD_PHONE;
import static com.github.longkerdandy.burger.backend.repository.UserRepository.FIELD_USERNAME;
import static org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.WildcardIndex;

/**
 * MongoDB Configuration.
 */
@Configuration
public class MongoConfig {

  private final MongoTemplate mongoTemplate;

  @Autowired
  public MongoConfig(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * Init MongoDB indexes.
   */
  @PostConstruct
  public void initIndexes() {
    // Wildcard Indexes
    // With this wildcard index, MongoDB indexes all fields for each document in the collection.
    // If a given field is a nested document or array, the wildcard index recurse into the
    // document/array and stores the value for all fields in the document/array.
    this.mongoTemplate.indexOps(COLLECTION_USERS).ensureIndex(new WildcardIndex());
    this.mongoTemplate.indexOps(COLLECTION_RESTAURANTS).ensureIndex(new WildcardIndex());
    this.mongoTemplate.indexOps(COLLECTION_REVIEWS).ensureIndex(new WildcardIndex());

    // Unique Indexes
    this.mongoTemplate
        .indexOps(COLLECTION_USERS)
        .ensureIndex(new Index().on(FIELD_USERNAME, Sort.Direction.ASC).unique());
    this.mongoTemplate
        .indexOps(COLLECTION_USERS)
        .ensureIndex(new Index().on(FIELD_EMAIL, Sort.Direction.ASC).unique());
    this.mongoTemplate
        .indexOps(COLLECTION_USERS)
        .ensureIndex(new Index().on(FIELD_PHONE, Sort.Direction.ASC).unique());

    // GEO Indexes
    this.mongoTemplate
        .indexOps(COLLECTION_RESTAURANTS)
        .ensureIndex(new GeospatialIndex(FIELD_LOCATION).typed(GEO_2DSPHERE));
  }
}
