package com.github.longkerdandy.burger.backend.repository;

import static java.time.Instant.now;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.github.longkerdandy.burger.backend.model.User;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for users related operations.
 */
@Slf4j
@Repository
public class UserRepository {

  // MongoDB Collections
  public static final String COLLECTION_USERS = "users";
  // MongoDB Fields
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_PHONE = "phone";
  @SuppressWarnings("unused")
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_AVATAR = "avatar";
  public static final String FIELD_NICKNAME = "nickname";
  public static final String FIELD_PROFILE = "profile";
  @SuppressWarnings("unused")
  public static final String FIELD_ROLES = "roles";
  public static final String FIELD_UPDATED_AT = "updatedAt";

  private final MongoTemplate mongo;              // Spring MongoTemplate
  private final PasswordEncoder pwdEncoder;       // Password Encoder

  @Autowired
  public UserRepository(MongoTemplate mongo, PasswordEncoder pwdEncoder) {
    this.mongo = mongo;
    this.pwdEncoder = pwdEncoder;
  }

  /**
   * Whether {@link User} already exist. This will check username, email and phone.
   *
   * @param user {@link User}
   * @return True if exists
   */
  public boolean isUserExists(User user) {
    Query query = new Query();
    query.addCriteria(new Criteria().orOperator(
        Criteria.where(FIELD_USERNAME).is(user.getUsername()),
        Criteria.where(FIELD_EMAIL).is(user.getEmail()),
        Criteria.where(FIELD_PHONE).is(user.getPhone())));
    return this.mongo.exists(query, COLLECTION_USERS);
  }

  /**
   * Whether {@link User} already exist. This only check username.
   *
   * @param username of {@link User}
   * @return True if exists
   */
  public boolean isUserExists(String username) {
    Query query = new Query();
    query.addCriteria(Criteria.where(FIELD_USERNAME).is(username));
    return this.mongo.exists(query, COLLECTION_USERS);
  }

  /**
   * Insert new {@link User} to database, throw exception if already exists.
   *
   * @param user {@link User} to be added
   * @return Inserted {@link User}
   */
  public User insertUser(User user) {
    // Encode password
    user.setPassword(this.pwdEncoder.encode(user.getPassword()));
    user.setUpdatedAt(now());
    return this.mongo.insert(user, COLLECTION_USERS);
  }

  /**
   * Get {@link User} based on the username.
   *
   * @param username Username
   * @return {@link User}
   */
  public User getUserByUsername(String username) {
    return this.mongo.findOne(
        new Query().addCriteria(where(FIELD_USERNAME).is(username)),
        User.class, COLLECTION_USERS);
  }

  /**
   * Update {@link User} based on the username.  {@link #FIELD_NICKNAME} and {@link #FIELD_PASSWORD}
   * and {@link #FIELD_ROLES} can not be modified.
   *
   * @param user {@link User}
   * @return {@link UpdateResult}
   */
  public UpdateResult updateUserByName(User user) {
    return this.mongo.updateFirst(
        new Query().addCriteria(Criteria.where(FIELD_USERNAME).is(user.getUsername())),
        new Update()
            .set(FIELD_EMAIL, user.getEmail())
            .set(FIELD_PHONE, user.getPhone())
            .set(FIELD_NICKNAME, user.getNickname())
            .set(FIELD_AVATAR, user.getAvatar())
            .set(FIELD_PROFILE, user.getProfile())
            .set(FIELD_UPDATED_AT, now()),
        User.class, COLLECTION_USERS);
  }

  /**
   * Delete {@link User} based on the username.
   *
   * @param username of {@link User}
   * @return {@link DeleteResult}
   */
  public DeleteResult deleteUserByName(String username) {
    return this.mongo.remove(
        new Query().addCriteria(Criteria.where(FIELD_USERNAME).is(username)),
        User.class, COLLECTION_USERS);
  }
}
