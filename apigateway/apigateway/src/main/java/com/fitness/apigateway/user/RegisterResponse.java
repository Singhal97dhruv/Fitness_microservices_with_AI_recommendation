package com.fitness.apigateway.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterResponse {

    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
