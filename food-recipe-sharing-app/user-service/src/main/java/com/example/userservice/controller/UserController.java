package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String email) {
        UserProfile userProfile = userService.getUserProfile(email);
        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable String email, @Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userService.updateUserProfile(email, userProfileDto));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<UserProfile>> createUserProfile(@Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile created successfully", userService.createUserProfile(userProfileDto)));
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable String email) {
        userService.deleteUserProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile deleted successfully", null));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<UserProfile>>> getAllProfiles() {
        return ResponseEntity.ok(new ApiResponse<>(true, "User profiles retrieved successfully", userService.getAllProfiles()));
    }
}
