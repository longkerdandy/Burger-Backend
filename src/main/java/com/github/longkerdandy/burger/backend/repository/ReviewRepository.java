package com.github.longkerdandy.burger.backend.repository;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.ifNull;

import com.github.longkerdandy.burger.backend.model.Author;
import com.github.longkerdandy.burger.backend.model.Comment;
import com.github.longkerdandy.burger.backend.model.Rating;
import com.github.longkerdandy.burger.backend.model.Restaurant;
import com.github.longkerdandy.burger.backend.model.Review;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Size;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for review related operations.
 */
@Slf4j
@Repository
public class ReviewRepository {

  // MongoDB Collections
  public static final String COLLECTION_RESTAURANTS = "restaurants";
  public static final String COLLECTION_REVIEWS = "reviews";
  // MongoDB Fields
  public static final String FIELD_ID = "_id";
  public static final String FIELD_RESTAURANT_ID = "restaurantId";
  public static final String FIELD_AUTHOR = "author";
  public static final String FIELD_AUTHOR_USERNAME = "author.username";
  public static final String FIELD_TASTE = "taste";
  public static final String FIELD_TEXTURE = "texture";
  public static final String FIELD_VIRTUAL = "virtual";
  public static final String FIELD_CONTENT = "content";
  public static final String FIELD_IMAGES = "images";
  public static final String FIELD_COMMENTS = "comments";
  public static final String FIELD_COMMENTS_COUNT = "commentsCount";
  public static final String FIELD_TASTE_TOTAL = "rating.tasteTotal";
  public static final String FIELD_TASTE_COUNT = "rating.tasteCount";
  public static final String FIELD_TEXTURE_TOTAL = "rating.textureTotal";
  public static final String FIELD_TEXTURE_COUNT = "rating.textureCount";
  public static final String FIELD_VIRTUAL_TOTAL = "rating.virtualTotal";
  public static final String FIELD_VIRTUAL_COUNT = "rating.virtualCount";
  public static final String FIELD_UPDATED_AT = "updatedAt";


  private final MongoTemplate mongo;              // Spring MongoTemplate

  /**
   * Constructor.
   */
  @Autowired
  public ReviewRepository(MongoTemplate mongo) {
    this.mongo = mongo;
  }

  /**
   * Whether {@link Review} already exist. This will check id.
   *
   * @param id of {@link Review}
   * @return True if exists
   */
  public boolean isReviewExists(ObjectId id) {
    return this.mongo.exists(new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        Review.class, COLLECTION_REVIEWS);
  }

  /**
   * Insert new {@link Review} to database.
   *
   * @param review {@link Review} to be added
   * @return Inserted {@link Review}
   */
  public Review insertReview(Review review) {
    // Insert review as new record
    review.setUpdatedAt(now());
    review = this.mongo.insert(review, COLLECTION_REVIEWS);
    // Update rating for the restaurant
    updateRestaurantRating(review.getRestaurantId(), review.getTaste(), 1, review.getTexture(), 1,
        review.getVirtual(), 1);
    return review;
  }

  /**
   * Whether the specific {@link Review} was written by specific {@link Author}.
   *
   * @param id       of {@link Review}
   * @param username of {@link Author}
   * @return True if {@link Review} was written by the {@link Author}.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isReviewAuthor(ObjectId id, String username) {
    return this.mongo.exists(
        new Query().addCriteria(
            new Criteria().andOperator(
                Criteria.where(FIELD_ID).is(id),
                Criteria.where(FIELD_AUTHOR_USERNAME).is(username))),
        Review.class, COLLECTION_REVIEWS);
  }

  /**
   * Find the {@link Review} with specific id.
   *
   * @param id of {@link Review}
   * @return {@link Review}
   */
  public Review findReviewById(ObjectId id) {
    MatchOperation matchOp = Aggregation.match(Criteria.where(FIELD_ID).is(id));
    ProjectionOperation projectionOp = Aggregation
        .project(FIELD_ID, FIELD_RESTAURANT_ID, FIELD_AUTHOR, FIELD_TASTE, FIELD_TEXTURE,
            FIELD_VIRTUAL, FIELD_CONTENT, FIELD_IMAGES, FIELD_COMMENTS)
        .and(Size.lengthOfArray(ifNull(FIELD_COMMENTS).then(emptyList()))).as(FIELD_COMMENTS_COUNT);
    Aggregation aggregation = Aggregation.newAggregation(matchOp, projectionOp);
    List<Review> results = this.mongo.aggregate(aggregation, COLLECTION_REVIEWS, Review.class)
        .getMappedResults();
    return results.isEmpty() ? null : results.get(0);
  }

  /**
   * Find the {@link Review} for the specific {@link Restaurant}.
   *
   * @param restaurantId id of {@link Restaurant}
   * @param direction    Sort {@link Direction}
   * @param skip         Records to be skipped
   * @param limit        Return size limit
   * @return List of {@link Review}s
   */
  public List<Review> findReviewByRestaurant(ObjectId restaurantId, Direction direction, long skip,
      int limit) {
    Query query = new Query()
        .addCriteria(Criteria.where(FIELD_RESTAURANT_ID).is(restaurantId))
        .with(Sort.by(direction, FIELD_ID))
        .skip(skip)
        .limit(limit);
    query.fields().exclude(FIELD_RESTAURANT_ID, FIELD_COMMENTS, FIELD_UPDATED_AT);
    return this.mongo.find(query, Review.class, COLLECTION_REVIEWS);
  }

  /**
   * Update the {@link Review} with specific id. Only {@link #FIELD_CONTENT} and
   * {@link #FIELD_IMAGES} can be modified.
   *
   * @param review {@link Review}
   * @return {@link UpdateResult}
   */
  public UpdateResult updateReviewById(Review review) {
    return this.mongo.updateFirst(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(review.getId())),
        new Update()
            .set(FIELD_CONTENT, review.getContent())
            .set(FIELD_IMAGES, review.getImages())
            .set(FIELD_UPDATED_AT, now()),
        Restaurant.class, COLLECTION_REVIEWS);
  }

  /**
   * Add new {@link Comment} to the specific {@link Review}.
   *
   * @param id      of {@link Review}
   * @param comment {@link Comment} to be added
   * @return {@link UpdateResult}
   */
  public UpdateResult addReviewComment(ObjectId id, Comment comment) {
    comment.setCreatedAt(now());
    return this.mongo.updateFirst(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        new Update()
            .push(FIELD_COMMENTS, comment)
            .set(FIELD_UPDATED_AT, now()),
        Restaurant.class, COLLECTION_REVIEWS);
  }

  /**
   * Delete the {@link Review} with specific id.
   *
   * @param id of {@link Review}
   * @return {@link DeleteResult}
   */
  public DeleteResult deleteReviewById(ObjectId id) {
    // Load review from database
    Query query = new Query().addCriteria(Criteria.where(FIELD_ID).is(id));
    query.fields().exclude(FIELD_COMMENTS);
    Review review = this.mongo.findOne(query, Review.class, COLLECTION_REVIEWS);
    // Update rating for the restaurant
    if (review != null) {
      updateRestaurantRating(review.getRestaurantId(), -review.getTaste(), -1,
          -review.getTexture(), -1, -review.getVirtual(), -1);
    }
    // Try to delete the review anyway
    return this.mongo.remove(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(id)),
        Review.class, COLLECTION_REVIEWS);
  }

  /**
   * Update {@link Restaurant}'s {@link Rating} in atomic manner.
   */
  protected void updateRestaurantRating(ObjectId restaurantId, int tasteTotalInc, int tasteCountInc,
      int textureTotalInc, int textureCountInc, int virtualTotalInc, int virtualCountInc) {
    this.mongo.updateFirst(
        new Query().addCriteria(Criteria.where(FIELD_ID).is(restaurantId)),
        new Update()
            .inc(FIELD_TASTE_TOTAL, tasteTotalInc)
            .inc(FIELD_TASTE_COUNT, tasteCountInc)
            .inc(FIELD_TEXTURE_TOTAL, textureTotalInc)
            .inc(FIELD_TEXTURE_COUNT, textureCountInc)
            .inc(FIELD_VIRTUAL_TOTAL, virtualTotalInc)
            .inc(FIELD_VIRTUAL_COUNT, virtualCountInc)
            .set(FIELD_UPDATED_AT, now()),
        Restaurant.class, COLLECTION_RESTAURANTS);
  }
}
