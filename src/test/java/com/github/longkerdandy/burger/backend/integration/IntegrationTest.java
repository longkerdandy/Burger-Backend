package com.github.longkerdandy.burger.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpStatus.OK;

import com.github.longkerdandy.burger.backend.dto.request.UserRequest;
import com.github.longkerdandy.burger.backend.dto.response.DeleteResponse;
import com.github.longkerdandy.burger.backend.dto.response.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"test"})
public class IntegrationTest {

  // JWT access token
  private static String accessToken;

  @LocalServerPort
  private int port;                               // Web server port
  private final TestRestTemplate restTemplate;    // HTTP Client

  @Autowired
  public IntegrationTest(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @DisplayName("Insert new user test")
  @Test
  @Order(1)
  void userRegisterTest() {
    UserRequest userRequest = new UserRequest()
        .setUsername("loki")
        .setEmail("loki@hotlook.com")
        .setPhone("16621523681")
        .setPassword("8K2#AkQH")
        .setNickname("Loki Shi")
        .setProfile("A cute boy");
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
        "http://localhost:" + port + "/api/auth/register",
        userRequest,
        LoginResponse.class);
    assertEquals(OK, response.getStatusCode());
    LoginResponse loginResponse = response.getBody();
    assertNotNull(loginResponse);
    accessToken = loginResponse.getAccessToken();
  }

  @DisplayName("Delete user test")
  @Test
  @Order(2)
  void userDeleteTest() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    ResponseEntity<DeleteResponse> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/users/loki",
        DELETE,
        entity,
        DeleteResponse.class);
    assertEquals(OK, response.getStatusCode());
    DeleteResponse deleteResponse = response.getBody();
    assertNotNull(deleteResponse);
    assertEquals(1, deleteResponse.getDeleted());
  }
}
