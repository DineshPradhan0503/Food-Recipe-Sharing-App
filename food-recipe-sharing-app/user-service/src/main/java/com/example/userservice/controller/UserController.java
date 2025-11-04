package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserService;
import com.example.userservice.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        String email = JwtUtil.extractEmail(token);
        UserProfile userProfile = userService.getUserProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile retrieved successfully", userProfile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateCurrentUserProfile(@RequestHeader("Authorization") String token, @Valid @RequestBody UserProfileDto userProfileDto) {
        String email = JwtUtil.extractEmail(token);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile updated successfully", userService.updateUserProfile(email, userProfileDto)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAllProfiles(@RequestHeader("Authorization") String token) {
        SecurityUtil.ensureAdmin(token);
        return ResponseEntity.ok(new ApiResponse<>(true, "All user profiles retrieved successfully", userService.getAllProfiles()));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable String email, @RequestHeader("Authorization") String token) {
        SecurityUtil.ensureAdmin(token);
        userService.deleteUserProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile deleted successfully", null));
    }

    @GetMapping("/admin-data")
    public ResponseEntity<ApiResponse<String>> getAdminData(@RequestHeader("Authorization") String token) {
        SecurityUtil.ensureAdmin(token);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin data retrieved successfully", "This is a secret admin message!"));
    }
}
