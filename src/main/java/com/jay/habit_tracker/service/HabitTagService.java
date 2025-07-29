package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.tag.HabitTagDto;

public interface HabitTagService {
    HabitTagDto addTagToHabit(Long habitId, String tagName);
}
