package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
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
    public ResponseEntity<ApiResponse<UserProfile>> getCurrentUserProfile(@RequestHeader("X-User-Email") String email) {
        UserProfile userProfile = userService.getUserProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile retrieved successfully", userProfile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateCurrentUserProfile(@RequestHeader("X-User-Email") String email, @Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile updated successfully", userService.updateUserProfile(email, userProfileDto)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAllProfiles(@RequestHeader("X-User-Roles") String roles) {
        SecurityUtil.ensureAdmin(roles);
        return ResponseEntity.ok(new ApiResponse<>(true, "All user profiles retrieved successfully", userService.getAllProfiles()));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable String email, @RequestHeader("X-User-Roles") String roles) {
        SecurityUtil.ensureAdmin(roles);
        userService.deleteUserProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile deleted successfully", null));
    }

    @GetMapping("/admin-data")
    public ResponseEntity<ApiResponse<String>> getAdminData(@RequestHeader("X-User-Roles") String roles) {
        SecurityUtil.ensureAdmin(roles);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin data retrieved successfully", "This is a secret admin message!"));
    }
}
