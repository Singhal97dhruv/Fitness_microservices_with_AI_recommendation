package com.fitness.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;


/**
 * -----------------------------------------------------------
 * 🔹 SecurityConfig
 * -----------------------------------------------------------
 * This class configures Spring Security for the API Gateway.
 * Since the Gateway is reactive (WebFlux-based), it uses
 * SecurityWebFilterChain instead of the traditional SecurityFilterChain.
 *
 * Responsibilities:
 * 1️⃣ Disable CSRF (not required for stateless REST APIs).
 * 2️⃣ Require all incoming requests to be authenticated.
 * 3️⃣ Integrate OAuth2 Resource Server with JWT validation
 *     — this ensures that only valid tokens issued by Keycloak
 *       (or any configured identity provider) are accepted.
 * -----------------------------------------------------------
 */


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Defines the main reactive security filter chain for the Gateway.
     *
     * @param http the ServerHttpSecurity object used to build the security chain
     * @return SecurityWebFilterChain defining security behavior for all requests
     */


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        return http
                // Disable Cross-Site Request Forgery since we’re working with stateless JWT tokens
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Require authentication for ALL routes through the API Gateway
                .authorizeExchange(exchange->exchange.anyExchange().authenticated())

                // Configure the Gateway as an OAuth2 Resource Server using JWT validation
                // Spring Security automatically validates the token using the public keys from Keycloak
                .oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE","OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","X-User-ID"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**",corsConfiguration);
        return  source;

    }

}
