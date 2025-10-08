package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.RegisterResponse;

import java.util.List;

public interface UserService {

    public RegisterResponse register(RegisterRequest userRequest);
    public List<RegisterResponse> getAllUsers();
    public Boolean validateUser(String userId);
}
