package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_tag.HabitTagDto;
import com.jay.habit_tracker.entity.HabitTag;

public interface HabitTagService {
    HabitTagDto addTagToHabit(Long habitId, String tagName);
}
