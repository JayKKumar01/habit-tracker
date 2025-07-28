package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.profile.ProfileRequest;
import com.jay.habit_tracker.dto.profile.ProfileResponse;
import com.jay.habit_tracker.entity.Profile;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.repository.ProfileRepository;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public ProfileResponse saveOrUpdate(Long userId, ProfileRequest profileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElse(new Profile());

        profile.setUser(user);
        profile.setBio(profileRequest.getBio());

        profileRepository.save(profile);

        return new ProfileResponse(user.getEmail(), profile.getBio());
    }


    @Override
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return new ProfileResponse(user.getEmail(),profile.getBio());
    }
}

