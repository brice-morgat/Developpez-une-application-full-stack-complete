package com.openclassrooms.mddapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final int MIN_KEY_BYTES = 32;

  private final SecretKey secretKey;
  private final long expirationMs;

  public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
    this.secretKey = buildSigningKey(secret);
    this.expirationMs = expirationMs;
  }

  public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
    Instant issuedAt = Instant.now();
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(issuedAt.plusMillis(expirationMs)))
        .signWith(secretKey)
        .compact();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    Claims claims = extractAllClaims(token);
    return userDetails.getUsername().equals(claims.getSubject()) && claims.getExpiration().after(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey buildSigningKey(String rawSecret) {
    byte[] keyBytes = decodeSecret(rawSecret);

    if (keyBytes.length < MIN_KEY_BYTES) {
      throw new IllegalStateException("app.jwt.secret must contain at least 32 bytes");
    }

    return Keys.hmacShaKeyFor(keyBytes);
  }

  private byte[] decodeSecret(String rawSecret) {
    try {
      return Decoders.BASE64.decode(rawSecret);
    } catch (RuntimeException ignored) {
      return rawSecret.getBytes(StandardCharsets.UTF_8);
    }
  }
}
