package com.github.longkerdandy.burger.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SpringBoot application entry point.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class Application {

  /**
   * Main.
   *
   * @param args arguments
   */
  @SuppressWarnings("resource")
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
