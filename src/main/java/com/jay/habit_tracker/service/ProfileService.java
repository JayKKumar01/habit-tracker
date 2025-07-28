package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.profile.ProfileUpdateDto;

public interface ProfileService {
    ProfileUpdateDto saveOrUpdate(Long email, ProfileUpdateDto bio);
    ProfileUpdateDto getProfile(Long email);
}
