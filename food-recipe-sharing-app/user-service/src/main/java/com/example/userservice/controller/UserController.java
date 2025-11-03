package com.example.userservice.controller;

import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
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
}
