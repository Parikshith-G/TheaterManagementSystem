package com.api.filter;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import com.api.util.Jwtutil;

import org.springframework.http.HttpMethod;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private Jwtutil jwtUtil;

	@Autowired
	private RouteValidator validator;

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (validator.isSecured.test(exchange.getRequest())) {
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					logger.error("Missing Authorization header");
					throw new RuntimeException("Missing Authorization header");
				}
				String authHeader = Objects
						.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
				HttpMethod method = exchange.getRequest().getMethod();

				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}

				try {
					jwtUtil.validateToken(authHeader);
					String role = jwtUtil.extractRole(authHeader);
					String path = exchange.getRequest().getURI().getPath();

					if (!checkRoleAccess(role, path, method)) {
						logger.error("Unauthorized access: Role '{}' does not have access to path '{}'", role, path);
						throw new RuntimeException("Unauthorized access");
					}
				} catch (Exception e) {
					logger.error("Invalid access: {}", e.getMessage());
					throw new RuntimeException("Unauthorized access to the application");
				}
			}
			return chain.filter(exchange);
		});
	}

	private boolean checkRoleAccess(String role, String path, HttpMethod method) {
		if (role.equals("ADMIN") || role.equals("GOD")) {
			return true;
		} else if (role.equals("USER")) {

			return path.startsWith("/api/v1/") && (method.equals(HttpMethod.GET) || method.equals(HttpMethod.POST));

		}
		return false; // Default: deny access
	}

	public static class Config {

	}

}