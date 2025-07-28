package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.profile.ProfileRequest;
import com.jay.habit_tracker.dto.profile.ProfileResponse;

public interface ProfileService {
    ProfileResponse saveOrUpdate(Long email, ProfileRequest bio);
    ProfileResponse getProfile(Long email);
}
