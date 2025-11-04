package com.example.authservice.controller;

import com.example.authservice.dto.ApiResponse;
import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        final String jwt = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", new JwtResponse(jwt, "Login successful")));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> saveUser(@Valid @RequestBody UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Email is already taken", null));
        }
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setRoles(Collections.singletonList("USER"));
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", userRepository.save(newUser)));
    }
}
