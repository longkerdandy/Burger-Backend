package com.github.longkerdandy.burger.backend.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.longkerdandy.burger.backend.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * JWT toolkit.
 */
@Slf4j
@Component
public class JwtToolkits {

  private final SecretKey secretKey;    // JWT secret

  @Value("${security.jwt.expiration}")
  private int expiration;   // JWT token expire in seconds

  @Autowired
  public JwtToolkits(@Value("${security.jwt.secret}") String secretStr) {
    this.secretKey = Keys.hmacShaKeyFor(secretStr.getBytes(UTF_8));
  }

  /**
   * Generate JWT token based on the {@link Authentication}.
   *
   * @param authentication Represents the token for an authentication request or for an
   *                       authenticated principal once the request has been processed by the
   *                       AuthenticationManager.authenticate(Authentication) method.
   * @return JWT token
   */
  public String generateJwtToken(Authentication authentication) {
    // Get user information from authentication
    User user = (User) authentication.getPrincipal();
    // Generate JWT token with JJWT SDK
    return generateJwtToken(user);
  }

  /**
   * Generate JWT token based on the {@link User}.
   *
   * @param user {@link User}
   * @return JWT token
   */
  public String generateJwtToken(User user) {
    // Generate JWT token with JJWT SDK
    Date issuedAt = new Date();
    return Jwts.builder()
        .setIssuer("Dennis Gu")
        .setSubject(user.getUsername())  // username as subject
        .setExpiration(new Date(issuedAt.getTime() + this.expiration * 1000L))
        .setIssuedAt(issuedAt)
        .signWith(this.secretKey, SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Get {@link User}'s username from JWT token.
   *
   * @param token JWT token
   * @return {@link User}'s username
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(this.secretKey).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Get expiration timestamp from JWT token.
   *
   * @param token JWT token
   * @return Expiration timestamp
   */
  public Date getExpiresAtFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(this.secretKey).build()
        .parseClaimsJws(token).getBody().getExpiration();
  }

  /**
   * Validate the JWT token.
   *
   * @param token JWT token
   * @return True if valid
   */
  public boolean validateJwtToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      log.warn("JWT token has invalid JWT signature: ", e);
    } catch (IllegalArgumentException e) {
      log.warn("JWT token's claims string is empty: ", e);
    }
    return false;
  }
}
