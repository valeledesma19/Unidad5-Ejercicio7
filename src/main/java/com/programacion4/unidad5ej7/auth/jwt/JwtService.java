package com.programacion4.unidad5ej7.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final int MIN_SECRET_BYTES = 32;

	private final JwtProperties properties;

	private SecretKey signingKey;

	@PostConstruct
	void initSigningKey() {
		if (!"HS256".equalsIgnoreCase(properties.algorithm())) {
			throw new IllegalStateException("Solo HS256 soportado");
		}

		byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);

		if (keyBytes.length < MIN_SECRET_BYTES) {
			throw new IllegalStateException("Secret muy corto");
		}

		signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(String username, Collection<String> roles) {
		return buildToken(username, roles, properties.accessExpirationMs(), "ACCESS");
	}

	public String generateRefreshToken(String username) {
		return buildToken(username, List.of(), properties.refreshExpirationMs(), "REFRESH");
	}

	private String buildToken(String username, Collection<String> roles, long expirationMs, String type) {

		Instant now = Instant.now();

		return Jwts.builder()
				.subject(username)
				.claim(JwtClaimNames.ROLES, roles)
				.claim(JwtClaimNames.TOKEN_TYPE, type)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(expirationMs)))
				.signWith(signingKey, Jwts.SIG.HS256)
				.compact();
	}

	public Optional<Claims> parseValidClaims(String token) {
		try {
			return Optional.of(
					Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload()
			);
		} catch (JwtException e) {
			return Optional.empty();
		}
	}
}
