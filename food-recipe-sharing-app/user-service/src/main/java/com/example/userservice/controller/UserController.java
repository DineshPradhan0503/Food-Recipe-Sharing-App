package com.example.userservice.controller;

import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String username) {
        UserProfile userProfile = userService.getUserProfile(username);
        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfile);
    }

import javax.validation.Valid;
//...
    @PutMapping("/{username}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable String username, @Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userService.updateUserProfile(username, userProfileDto));
    }
}
