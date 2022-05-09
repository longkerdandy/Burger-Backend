package com.github.longkerdandy.burger.backend.mapper;

import com.github.longkerdandy.burger.backend.dto.request.ReviewRequest;
import com.github.longkerdandy.burger.backend.dto.response.ReviewResponse;
import com.github.longkerdandy.burger.backend.model.Review;
import java.time.Instant;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for {@link Review}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

  /**
   * {@link ObjectId} to String.
   */
  @Named("objIdToStr")
  static String objIdToStr(ObjectId id) {
    return id != null ? id.toHexString() : null;
  }

  /**
   * {@link ObjectId} to {@link Instant}.
   */
  @Named("objIdToInstant")
  static Instant objIdToInstant(ObjectId id) {
    return id != null ? id.getDate().toInstant() : null;
  }

  /**
   * Mapping from {@link ReviewRequest} to for {@link Review}.
   *
   * @param request {@link ReviewRequest}
   * @return Mapped {@link Review}
   */
  Review requestToReview(ReviewRequest request);

  /**
   * Mapping from {@link Review} to for {@link ReviewResponse}.
   *
   * @param review {@link Review}
   * @return Mapped {@link ReviewResponse}
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "objIdToStr")
  @Mapping(source = "restaurantId", target = "restaurantId", qualifiedByName = "objIdToStr")
  @Mapping(source = "id", target = "createdAt", qualifiedByName = "objIdToInstant")
  ReviewResponse reviewToResponse(Review review);
}
