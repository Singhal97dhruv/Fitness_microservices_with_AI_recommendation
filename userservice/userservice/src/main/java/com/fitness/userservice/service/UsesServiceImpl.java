package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.RegisterResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UsesServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelmapper;

    @Override
    public RegisterResponse register(RegisterRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())){
            User existingUser=userRepository.findByEmail(userRequest.getEmail());
            RegisterResponse registerResponse=modelmapper.map(existingUser,RegisterResponse.class);
            return registerResponse;
        }

        User user=modelmapper.map(userRequest,User.class);
        user.setId(null);
        User savedUser=userRepository.save(user);
        RegisterResponse registerResponse=modelmapper.map(savedUser,RegisterResponse.class);
        return registerResponse;
    }

    @Override
    public List<RegisterResponse> getAllUsers() {

        List<User>users=userRepository.findAll();

        List<RegisterResponse>userResponses=new ArrayList<>();
        userResponses= users.stream().map(user -> modelmapper.map(user,RegisterResponse.class)).toList();
        return userResponses;
    }

    @Override
    public Boolean validateUser(String userId) {
        return userRepository.existsByKeycloakId(userId);
    }
}
