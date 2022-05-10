package com.github.longkerdandy.burger.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.longkerdandy.burger.backend.model.User;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests for {@link UserRepository}
 */
@ActiveProfiles(profiles = {"test"})
@DataMongoTest
public class UserRepositoryTest {

  private static UserRepository userRepo;
  private static final PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserRepositoryTest(MongoTemplate mongo) {
    userRepo = new UserRepository(mongo, pwdEncoder);
  }

  @DisplayName("Insert new user test")
  @Test
  @Order(1)
  void insertUserTest() {
    User user = new User()
        .setUsername("loki")
        .setEmail("loki@hotlook.com")
        .setPhone("16621523681")
        .setPassword("8K2#AkQH")
        .setNickname("Loki Shi")
        .setProfile("A cute boy");
    user = userRepo.insertUser(user);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertTrue(pwdEncoder.matches("8K2#AkQH", user.getPassword()));
  }

  @DisplayName("Check user exist test")
  @Test
  @Order(2)
  void checkUserExistTest() {
    assertTrue(userRepo.isUserExists("loki"));
    assertFalse(userRepo.isUserExists("not-exist-username"));
  }

  @DisplayName("Delete user test")
  @Test
  @Order(3)
  void deleteUserTest() {
    DeleteResult result = userRepo.deleteUserByName("loki");
    assertEquals(1, result.getDeletedCount());
  }
}
