package org.dalipaj.apigateway.auth.config;

import org.dalipaj.apigateway.application.ApplicationService;
import org.dalipaj.apigateway.auth.service.impl.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.rateLimit.service.RateLimitProperties;
import org.dalipaj.apigateway.upstream.service.impl.UpstreamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize ->
				authorize
				.requestMatchers(addAsteriskToEndpoint(ApplicationService.ENDPOINT),
								 addAsteriskToEndpoint(UpstreamService.ENDPOINT),
								 addAsteriskToEndpoint(RateLimitProperties.ENDPOINT))
						.authenticated()
				.anyRequest()
						.permitAll());

		http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http.exceptionHandling(e ->
				e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.NOT_FOUND)));
		http.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.cors(AbstractHttpConfigurer::disable)
				.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}

	private static String addAsteriskToEndpoint(String endpoint) {
		return endpoint + "/**";
	}
}