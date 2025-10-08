package com.fitness.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
