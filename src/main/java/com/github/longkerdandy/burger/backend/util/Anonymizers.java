package com.github.longkerdandy.burger.backend.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

/**
 * Anonymizer util.
 */
@UtilityClass
public class Anonymizers {

  private static final Pattern PATTERN_VALID_PHONE = Pattern.compile("\\d{11}");
  private static final Pattern PATTERN_VALID_EMAIL = Pattern.compile("^[A-Za-z\\d+_.-]+@(.+)$");

  /**
   * Try to anonymize 11-digit phone number.
   *
   * @param phone number
   * @return Anonymized phone number
   */
  public static String anonymizePhone(String phone) {
    if (phone != null) {
      Matcher matcher = PATTERN_VALID_PHONE.matcher(phone);
      if (matcher.find()) {
        return phone.replaceAll("\\d{7}(\\d{4})", "*******$1");
      }
    }
    return phone;
  }

  /**
   * Try to anonymize valid email.
   *
   * @param email address
   * @return Anonymized email address
   */
  public static String anonymizeEmail(String email) {
    if (email != null) {
      Matcher matcher = PATTERN_VALID_EMAIL.matcher(email);
      if (matcher.find()) {
        return email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
      }
    }
    return email;
  }
}
