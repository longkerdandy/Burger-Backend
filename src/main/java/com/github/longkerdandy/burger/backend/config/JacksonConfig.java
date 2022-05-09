package com.github.longkerdandy.burger.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.longkerdandy.burger.backend.util.Jacksons;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * The simplest way to override the default configuration is to define an ObjectMapper bean and to
 * mark it as @Primary. We should use this approach when we want to have full control over the
 * serialization process, and we don't want to allow external configuration.
 */
@Configuration
public class JacksonConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return Jacksons.getMapper();
  }
}
