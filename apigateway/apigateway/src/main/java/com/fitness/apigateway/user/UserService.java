package com.fitness.apigateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * -----------------------------------------------------------
 * 🔹 UserService (API Gateway Layer)
 * -----------------------------------------------------------
 * This service acts as a bridge between the API Gateway and
 * the User Microservice. It uses Spring WebClient (reactive)
 * to make non-blocking HTTP calls to the User Service.
 *
 * Responsibilities:
 * 1️⃣ Validate if a user exists in the User Service.
 * 2️⃣ Register a new user (when authenticated via Keycloak
 *     but not yet present in the User Service).
 *
 * It returns Mono<T> since WebFlux is reactive and async.
 * -----------------------------------------------------------
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    /**
     * The reactive HTTP client used to call the User Service.
     * It’s configured elsewhere (likely as a @Bean in WebClientConfig)
     * with the User Service’s base URL.
     */

    @Autowired
    private WebClient userWebClient;

    /**
     * -----------------------------------------------------------
     * ✅ validateUser(String userId)
     * -----------------------------------------------------------
     * Calls the User Service to check whether a given Keycloak
     * user ID exists in the User database.
     *
     * @param userId The unique Keycloak user identifier
     * @return Mono<Boolean> indicating whether user exists
     * -----------------------------------------------------------
     */

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling user service for {}",userId);
        try {
            return userWebClient.get()
                    // Dynamically inject the userId into the request path
                    .uri("/api/v1/users/{userId}/validate", userId)
                    // Perform GET call and retrieve the response body as Boolean
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    // Error handling: map different HTTP errors to meaningful messages
                    .onErrorResume(WebClientResponseException.class,e->{
                        if(e.getStatusCode()== HttpStatus.NOT_FOUND){
                            return Mono.error(new RuntimeException("User not found: "+userId));
                        }
                        else if(e.getStatusCode()== HttpStatus.BAD_REQUEST){
                            return Mono.error(new RuntimeException("Invalid request: "+userId));
                        }
                        return Mono.error(new RuntimeException("Unexpected error"));
                    });
        } catch (WebClientResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * -----------------------------------------------------------
     * ✅ registerUser(RegisterRequest registerRequest)
     * -----------------------------------------------------------
     * Calls the User Service to register a new user based on
     * Keycloak token details (email, name, etc.).
     *
     * @param registerRequest DTO containing registration info
     * @return Mono<RegisterResponse> with user data after creation
     * -----------------------------------------------------------
     */

    public Mono<RegisterResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling user service for {}",registerRequest.getEmail());
        try {
            return userWebClient.post()
                    // User registration endpoint in User Service
                    .uri("/api/v1/users/register")
                    // Send user details in the request body
                    .bodyValue(registerRequest)
                    // Retrieve response and map to RegisterResponse DTO
                    .retrieve()
                    .bodyToMono(RegisterResponse.class)
                    .onErrorResume(WebClientResponseException.class,e->{
                       if(e.getStatusCode()== HttpStatus.BAD_REQUEST){
                            return Mono.error(new RuntimeException("Bad request: "+e.getMessage()));
                        }
                        return Mono.error(new RuntimeException("Unexpected error "+ e.getMessage()));
                    });
        } catch (WebClientResponseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
