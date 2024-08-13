package com.login.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class Jwtutil {

	public static final long JWT_TOKEN_VALIDITY = 3_600_000;

	private static final Logger logger = LoggerFactory.getLogger(Jwtutil.class);

	String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	public String getUsernameFromToken(String token) {
		logger.debug("Getting username from token");
		return getClaimFromToken(token, Claims::getSubject);
	}

	public String getUserRoleFromToken(String token) {
		logger.debug("Getting role from token");
		Claims claims = getAllClaimsFromToken(token);
		return claims.get("role").toString();
	}

	public Date getExpirationDateFromToken(String token) {
		logger.debug("Getting expiration date from token");
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		logger.debug("Getting claims from token");
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public Claims getAllClaimsFromToken(String token) {
		logger.debug("Getting all claims from token");
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))).build()
				.parseClaimsJws(token).getBody();

	}

	public String generateToken(String userName, String role, Long id, String email, String phoneNumber) {
		logger.debug("Generating token for user: {}, role: {}", userName, role);
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("userName", userName);
		claims.put("email", email);
		claims.put("id", id);
		claims.put("phoneNumber", phoneNumber);
		return doGenerateToken(claims, userName);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		logger.debug("Creating token for subject: {}", subject);
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS512).compact();
	}

	public void validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))).build()
					.parseClaimsJws(token);
		} catch (JwtException e) {

			throw e;
		}
	}
}
