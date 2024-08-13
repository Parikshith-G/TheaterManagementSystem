package com.login.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtutilTest {
	@InjectMocks
	private Jwtutil jwtutil;

	private String token;
	private Claims claims;
	private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Ensure the secret is set correctly

		claims = Jwts.claims();
		claims.setSubject("testuser");
		claims.put("role", "USER");
		claims.put("userName", "testuser");
		claims.put("email", "test@example.com");
		claims.put("id", 123L);
		claims.put("phoneNumber", "1234567890");

		token = Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + Jwtutil.JWT_TOKEN_VALIDITY))
				.signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS512).compact();
	}

	@Test
	void testGetUsernameFromToken() {
		String username = jwtutil.getUsernameFromToken(token);
		assertEquals("testuser", username);
	}

	@Test
	void testGetUserRoleFromToken() {
		String role = jwtutil.getUserRoleFromToken(token);
		assertEquals("USER", role);
	}

	@Test
	void testGetExpirationDateFromToken() {
		Date expiration = jwtutil.getExpirationDateFromToken(token);
		assertNotNull(expiration);
	}

	@Test
	void testGetClaimFromToken() {
		Function<Claims, String> claimsResolver = Claims::getSubject;
		String subject = jwtutil.getClaimFromToken(token, claimsResolver);
		assertEquals("testuser", subject);
	}

	@Test
	void testGetAllClaimsFromToken() {
		Claims generatedClaims = claims; // Claims used to generate the token
		Claims parsedClaims = jwtutil.getAllClaimsFromToken(token);

		// Print claims for debugging
		System.out.println("Generated Claims:");
		generatedClaims.entrySet().forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

		System.out.println("Parsed Claims:");
		parsedClaims.entrySet().forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

		// Compare claims individually
		assertEquals(generatedClaims.getSubject(), parsedClaims.getSubject());
		assertEquals(generatedClaims.get("role"), parsedClaims.get("role"));
		assertEquals(generatedClaims.get("userName"), parsedClaims.get("userName"));
		assertEquals(generatedClaims.get("email"), parsedClaims.get("email"));
		assertEquals(generatedClaims.get("id"),Long.parseLong(parsedClaims.get("id").toString()));
		assertEquals(generatedClaims.get("phoneNumber"), parsedClaims.get("phoneNumber"));
		assertEquals(generatedClaims.getIssuedAt(), parsedClaims.getIssuedAt());
		assertEquals(generatedClaims.getExpiration(), parsedClaims.getExpiration());
	}

	@Test
	void testGenerateToken() {
		String generatedToken = jwtutil.generateToken("testuser", "USER", 123L, "test@example.com", "1234567890");
		assertNotNull(generatedToken);
	}

	@Test
	void testValidateToken_Valid() {
		assertDoesNotThrow(() -> jwtutil.validateToken(token));
	}

	@Test
	void testValidateToken_Invalid() {
		String invalidToken = token + "invalid";
		assertThrows(JwtException.class, () -> jwtutil.validateToken(invalidToken));
	}
}