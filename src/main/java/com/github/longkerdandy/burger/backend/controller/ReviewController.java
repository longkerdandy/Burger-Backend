package com.github.longkerdandy.burger.backend.controller;

import static com.github.longkerdandy.burger.backend.model.Role.ADMIN;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.github.longkerdandy.burger.backend.dto.request.CommentRequest;
import com.github.longkerdandy.burger.backend.dto.request.ReviewRequest;
import com.github.longkerdandy.burger.backend.dto.response.DeleteResponse;
import com.github.longkerdandy.burger.backend.dto.response.ReviewResponse;
import com.github.longkerdandy.burger.backend.dto.response.UpdateResponse;
import com.github.longkerdandy.burger.backend.mapper.CommentMapper;
import com.github.longkerdandy.burger.backend.mapper.ReviewMapper;
import com.github.longkerdandy.burger.backend.model.Author;
import com.github.longkerdandy.burger.backend.model.Comment;
import com.github.longkerdandy.burger.backend.model.Restaurant;
import com.github.longkerdandy.burger.backend.model.Review;
import com.github.longkerdandy.burger.backend.model.User;
import com.github.longkerdandy.burger.backend.repository.RestaurantRepository;
import com.github.longkerdandy.burger.backend.repository.ReviewRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Review controller.
 */
@Slf4j
@CrossOrigin
@RestController
@SuppressWarnings("unused")
public class ReviewController {

  private final ReviewMapper mapper;             // Review mapper
  private final CommentMapper cmtMapper;         // Comment mapper
  private final ReviewRepository repo;           // MongoDB
  private final RestaurantRepository rstRepo;    // MongoDB

  /**
   * Constructor.
   */
  @Autowired
  public ReviewController(ReviewMapper mapper, CommentMapper cmtMapper, ReviewRepository repo,
      RestaurantRepository rstRepo) {
    this.mapper = mapper;
    this.cmtMapper = cmtMapper;
    this.repo = repo;
    this.rstRepo = rstRepo;
  }

  /**
   * Create a new {@link Review}.
   *
   * @param request {@link ReviewRequest}
   * @return {@link ReviewResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @PostMapping("/api/reviews")
  public ResponseEntity<?> newReview(@Valid @RequestBody ReviewRequest request) {
    // Mapping from ReviewRequest to Review
    Review review = this.mapper.requestToReview(request);
    // Load user information from context and update author information
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    review.setAuthor(new Author().setUsername(user.getUsername()).setAvatar(user.getAvatar())
        .setNickname(user.getNickname()));
    // Check whether restaurant exists
    if (!this.rstRepo.isRestaurantExists(review.getRestaurantId())) {
      throw new ResponseStatusException(NOT_FOUND, "The restaurant doesn't exist.");
    }
    // Insert new review
    review = this.repo.insertReview(review);
    // Response
    return ResponseEntity.ok(this.mapper.reviewToResponse(review));
  }

  /**
   * Get the {@link Review} record with specific id.
   *
   * @param id of {@link Review}
   * @return {@link ReviewResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/reviews/{id}")
  public ResponseEntity<?> getReview(@Valid @NotNull @PathVariable ObjectId id) {
    // Load the review record
    Review review = this.repo.findReviewById(id);
    // Response
    return ResponseEntity.ok(this.mapper.reviewToResponse(review));
  }

  /**
   * Get the {@link Review} record with specific id.
   *
   * @param restaurantId id of {@link Restaurant}
   * @param direction    Sort direction
   * @param skip         Records to be skipped
   * @param limit        Result size limit
   * @return {@link ReviewResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/reviews")
  public ResponseEntity<?> searchReview(@Valid @NotNull @RequestParam ObjectId restaurantId,
      @RequestParam(defaultValue = "ASC") Direction direction,
      @RequestParam(defaultValue = "0") long skip,
      @RequestParam(defaultValue = "20") int limit) {
    // Load the review record
    List<Review> reviews = this.repo.findReviewByRestaurant(restaurantId, direction, skip, limit);
    // Response
    return ResponseEntity.ok(
        reviews.stream()
            .map(this.mapper::reviewToResponse)
            .collect(Collectors.toList()));
  }

  /**
   * Update the {@link Review} record with specific id.
   *
   * @param id      of {@link Review}
   * @param request {@link ReviewRequest}
   * @return {@link UpdateResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @PutMapping("/api/reviews/{id}")
  public ResponseEntity<?> updateReview(@Valid @NotNull @PathVariable ObjectId id,
      @Valid @RequestBody ReviewRequest request) {
    // Mapping from ReviewRequest to Review
    Review review = this.mapper.requestToReview(request);
    review.setId(id);
    // Load user information from context
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    // If user is not ADMIN, he can only update the review he posted
    if (!user.getRoles().contains(ADMIN)) {
      if (!this.repo.isReviewAuthor(id, user.getUsername())) {
        throw new ResponseStatusException(FORBIDDEN, "The review's author is someone else.");
      }
    }
    // Try to update the review record
    UpdateResult result = this.repo.updateReviewById(review);
    // Response
    return ResponseEntity.ok(
        new UpdateResponse()
            .setMatched(result.getMatchedCount())
            .setModified(result.getModifiedCount()));
  }

  /**
   * Add the {@link Comment} record to the specific {@link Review}.
   *
   * @param id      of {@link Review}
   * @param request {@link CommentRequest}
   * @return {@link UpdateResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @PostMapping("/api/reviews/{id}/comments")
  public ResponseEntity<?> addComment(@Valid @NotNull @PathVariable ObjectId id,
      @Valid @RequestBody CommentRequest request) {
    // Mapping from CommentRequest to Comment
    Comment comment = this.cmtMapper.requestToComment(request);
    // Load user information from context and update author information
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    comment.setAuthor(new Author().setUsername(user.getUsername()).setAvatar(user.getAvatar())
        .setNickname(user.getNickname()));
    // Check whether review exists
    if (!this.repo.isReviewExists(id)) {
      throw new ResponseStatusException(NOT_FOUND, "The review not exist.");
    }
    // Try to add comment to the review record
    UpdateResult result = this.repo.addReviewComment(id, comment);
    // Response
    return ResponseEntity.ok(
        new UpdateResponse()
            .setMatched(result.getMatchedCount())
            .setModified(result.getModifiedCount()));
  }

  /**
   * Delete the {@link Review} record with specific id.
   *
   * @return {@link DeleteResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @DeleteMapping("/api/reviews/{id}")
  public ResponseEntity<?> deleteReview(@Valid @NotNull @PathVariable ObjectId id) {
    // Load user information from context
    SecurityContext context = SecurityContextHolder.getContext();
    User user = (User) context.getAuthentication().getPrincipal();
    // If user is not ADMIN, he can only delete the review he posted
    if (!user.getRoles().contains(ADMIN)) {
      if (!this.repo.isReviewAuthor(id, user.getUsername())) {
        throw new ResponseStatusException(FORBIDDEN, "The review's author is someone else.");
      }
    }
    // Try to delete the restaurant record
    DeleteResult result = this.repo.deleteReviewById(id);
    // Response
    return ResponseEntity.ok(new DeleteResponse().setDeleted(result.getDeletedCount()));
  }
}
