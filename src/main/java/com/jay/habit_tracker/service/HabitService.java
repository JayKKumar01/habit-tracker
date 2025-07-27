package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;

import java.time.LocalDate;
import java.util.List;

public interface HabitService {
    HabitResponse createHabit(String userEmail, HabitRequest habitRequest);
    List<HabitResponse> getHabitsByUser(String userEmail);
    boolean softDeleteHabitByIdForUser(Long id, String email, LocalDate endDate);

    boolean editHabitByIdForUser(HabitEditRequest editRequest, String email);
}
