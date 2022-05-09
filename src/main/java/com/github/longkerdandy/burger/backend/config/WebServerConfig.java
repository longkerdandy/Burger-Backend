package com.github.longkerdandy.burger.backend.config;

import com.github.longkerdandy.burger.backend.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Web server configuration.
 */
@Configuration
public class WebServerConfig {

  /**
   * BCrypt password encoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Enable HTTP request logging.
   */
  @Bean
  public RequestLoggingFilter requestLoggingFilter() {
    RequestLoggingFilter loggingFilter = new RequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(64000);
    return loggingFilter;
  }
}
