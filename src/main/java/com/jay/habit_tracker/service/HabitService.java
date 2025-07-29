package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit.*;

import java.util.List;

public interface HabitService {
    HabitResponse createHabit(Long userId, HabitRequest habitRequest);
    HabitUpdateDto updateHabit(HabitUpdateDto updateDto);
    List<HabitEntitiesResponse> getHabitWithEntitiesByUserId(Long userId);
    boolean deleteHabit(Long habitId);

}
