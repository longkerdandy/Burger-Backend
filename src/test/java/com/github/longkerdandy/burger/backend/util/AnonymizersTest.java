package com.github.longkerdandy.burger.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Anonymizers}
 */
public class AnonymizersTest {

  @DisplayName("Valid phone number test")
  @Test
  void validPhoneTest() {
    String result = Anonymizers.anonymizePhone("18618618888");
    Assertions.assertEquals("*******8888", result);
  }

  @DisplayName("Invalid phone number test")
  @Test
  void invalidPhoneTest() {
    String result = Anonymizers.anonymizePhone("911");
    Assertions.assertEquals("911", result);
  }

  @DisplayName("Valid email test")
  @Test
  void validEmailTest() {
    String result = Anonymizers.anonymizeEmail("longkerdandy@gmail.com");
    Assertions.assertEquals("lon*********@gmail.com", result);
  }
}
