package com.fitness.userservice.services;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UserResponse register(RegisterRequest request) {

         if (repository.existsByEmail(request.getEmail())) {
             throw new RuntimeException("Email already exits! ");
         }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());

        User savedUser = repository.save(user);
        UserResponse UserResponse = new UserResponse();
        UserResponse.setId(savedUser.getId());
        UserResponse.setPassword(savedUser.getPassword());
        UserResponse.setEmail(savedUser.getEmail());
        UserResponse.setFirstName(savedUser.getFirstName());
        UserResponse.setLastName(savedUser.getLastName());
        UserResponse.setCreatedAt(savedUser.getCreatedAt());
        UserResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return UserResponse;

    }
}
