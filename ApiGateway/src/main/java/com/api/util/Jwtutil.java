package com.api.util;
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

@Component
public class Jwtutil {
	    //requirement :
	    public static final long JWT_TOKEN_VALIDITY = 3_600_000;

	    //    public static final long JWT_TOKEN_VALIDITY =  60;
	    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	    //retrieve username from jwt token
	    public String getUsernameFromToken(String token) {
	        return getClaimFromToken(token, Claims::getSubject);
	    }

	    //retrieve expiration date from jwt token
	    public Date getExpirationDateFromToken(String token) {
	        return getClaimFromToken(token, Claims::getExpiration);
	    }

	    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = getAllClaimsFromToken(token);
	        return claimsResolver.apply(claims);
	    }

	    //for retrieveing any information from token we will need the secret key
	    private Claims getAllClaimsFromToken(String token) {
	         /*return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();*/
	         return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))).build().parseClaimsJws(token).getBody();
	    	
	    }	

	    //check if the token has expired
	    private Boolean isTokenExpired(String token) {
	        final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date());
	    }

	    //generate token for user
	    public String generateToken(String userName,String role,String id,String email) {
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("role",role);
	        claims.put("id",userName);
	        claims.put("email", email);
	        claims.put("id", id);
	        return doGenerateToken(claims, userName);
	    }

	    //while creating the token -
	    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	    //2. Sign the JWT using the HS512 algorithm and secret key.
	    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	    //   compaction of the JWT to a URL-safe string
	    private String doGenerateToken(Map<String, Object> claims, String subject) {

	        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY ))
	                .signWith( Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)),SignatureAlgorithm.HS512).compact();
	    }

	    //validate token
	    public void validateToken(String token) {
	        try {
	        	Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))).build().parseClaimsJws(token);
	        }
	        catch(JwtException e) {
	        	throw e;
	        }
	    }
	    
	    public String extractRole(String token) {
	    	 try {
	             Claims claims = Jwts.parserBuilder()
	                 .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
	                 .build()
	                 .parseClaimsJws(token)
	                 .getBody();
	             String role = (String) claims.get("role");
	             return role;
	         } catch (JwtException e) {
	             throw e;
	         }
	    }
}
