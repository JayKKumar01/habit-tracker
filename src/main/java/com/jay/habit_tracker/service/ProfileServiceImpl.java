package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.ProfileRequest;
import com.jay.habit_tracker.dto.ProfileResponse;
import com.jay.habit_tracker.entity.Profile;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.ProfileMapper;
import com.jay.habit_tracker.repository.ProfileRepository;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse saveOrUpdate(String email, ProfileRequest profileRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseGet(() -> {
                    Profile newProfile = profileMapper.toEntity(profileRequest);
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setBio(profileRequest.getBio());

        return profileMapper.toResponse(profileRepository.save(profile));
    }


    @Override
    public ProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profileMapper.toResponse(profile);
    }
}
