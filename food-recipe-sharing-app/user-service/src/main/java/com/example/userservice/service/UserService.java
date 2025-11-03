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

    public UserProfile getUserProfile(String username) {
        return userProfileRepository.findByUsername(username).orElse(null);
    }

    public UserProfile updateUserProfile(String username, UserProfileDto userProfileDto) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(new UserProfile());
        userProfile.setUsername(username);
        userProfile.setEmail(userProfileDto.getEmail());
        userProfile.setBio(userProfileDto.getBio());
        return userProfileRepository.save(userProfile);
    }
}
