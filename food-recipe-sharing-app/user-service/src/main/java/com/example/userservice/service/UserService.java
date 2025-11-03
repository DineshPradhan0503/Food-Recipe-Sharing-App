package com.example.userservice.service;

import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.model.UserProfile;
import com.example.userservice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;

    public UserService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserProfile(String email) {
        return userProfileRepository.findByEmail(email).orElse(null);
    }

    public UserProfile updateUserProfile(String email, UserProfileDto userProfileDto) {
        UserProfile userProfile = userProfileRepository.findByEmail(email).orElse(new UserProfile());
        userProfile.setUsername(userProfileDto.getUsername());
        userProfile.setEmail(email);
        userProfile.setBio(userProfileDto.getBio());
        return userProfileRepository.save(userProfile);
    }
}
