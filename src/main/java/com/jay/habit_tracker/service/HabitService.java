package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;

import java.time.LocalDate;
import java.util.List;

public interface HabitService {
    HabitResponse createHabit(String userEmail, HabitRequest habitRequest);
    List<HabitResponse> getHabitsByUserId(Long userId);
    boolean editHabitByIdForUser(HabitEditRequest editRequest, String email);

    boolean deleteHabitByIdForUser(Long habitId, String email);
}
