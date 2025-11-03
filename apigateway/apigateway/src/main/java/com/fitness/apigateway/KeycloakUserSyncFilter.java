package com.fitness.apigateway;

import com.fitness.apigateway.user.RegisterRequest;
import com.fitness.apigateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.Duration;


/**
 * -----------------------------------------------------------
 * 🔹 KeycloakUserSyncFilter
 * -----------------------------------------------------------
 * This reactive WebFilter intercepts every incoming HTTP request
 * passing through the API Gateway. It performs the following:
 *
 * 1️⃣ Extracts the Keycloak JWT token from the "Authorization" header.
 * 2️⃣ Parses the token to fetch user details (email, name, Keycloak ID).
 * 3️⃣ Checks with the UserService if the user already exists in the local DB.
 * 4️⃣ If not found, it auto-registers the user in the UserService.
 * 5️⃣ Adds the user’s Keycloak ID into the request header ("X-User-ID")
 *     and forwards it to downstream microservices.
 *
 * This ensures seamless Keycloak <-> local user synchronization.
 * -----------------------------------------------------------
 */


@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    // Service used to validate and register users in UserService microservice
    @Autowired
    private UserService userService;

    /**
     * Core filtering logic executed for every incoming request.
     *
     * @param exchange - represents the current server web exchange (request + response)
     * @param chain    - allows continuation of the WebFlux filter chain
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Extract userId (if already present) and Keycloak JWT token from headers
        String userId=exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");

        // Parse token to extract user details into a RegisterRequest object
        RegisterRequest registerRequest=getUserDetails(token);

        // If userId was not passed, use Keycloak ID from the JWT claims
        if(userId==null){
            userId=registerRequest.getKeycloakId();
        }

        // If both userId and token exist, proceed with validation & registration
        log.info("tokken "+token);
        log.info("userId "+userId);
        if(userId!=null && token!=null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist->{
                        if(!exist){
                            if(registerRequest!=null){
                                log.info("Registering User");
                                return userService.registerUser(registerRequest);
                            }else {
                                log.info("SEnding back empty");
                                return Mono.empty();
                            }
                        }else {
                            log.info("User already exists, Skipping sync");
                            return Mono.empty();
                        }
                    })
                    // After validation/registration, forward the request downstream
                    .then(Mono.defer(()->{
                        // Add "X-User-ID" header to identify user in downstream services
                        ServerHttpRequest mutatedRequest=exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return chain.filter(exchange);
    }

    /**
     * Extracts user details from a Keycloak-issued JWT token.
     *
     * @param token - JWT access token from Authorization header
     * @return RegisterRequest containing user info for registration
     */
    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer=token.replace("Bearer","").trim();
            SignedJWT signedJWT=SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims= signedJWT.getJWTClaimsSet();

            RegisterRequest request=new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            request.setPassword("dummy@123");

            return request;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
