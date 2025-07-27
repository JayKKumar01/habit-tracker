package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.ProfileRequest;
import com.jay.habit_tracker.dto.ProfileResponse;

public interface ProfileService {
    ProfileResponse saveOrUpdate(String email, ProfileRequest bio);

    ProfileResponse getProfileByEmail(String email);
}
