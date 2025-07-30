package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.tag.TagDto;

public interface TagService {
    TagDto addHabitTag(Long habitId, String name);
    void removeHabitTag(Long habitId, Long tagId);
}
