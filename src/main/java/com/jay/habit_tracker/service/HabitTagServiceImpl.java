package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_tag.HabitTagDto;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.HabitTag;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.HabitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HabitTagServiceImpl implements HabitTagService {

    private final HabitRepository habitRepository;
    private final HabitTagRepository habitTagRepository;

    @Override
    public HabitTagDto addTagToHabit(Long habitId, String tagName) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found with id: " + habitId));

        String normalizedTag = tagName.trim().toLowerCase();

        HabitTag tag = habitTagRepository.findByName(normalizedTag)
                .orElseGet(() -> habitTagRepository.save(HabitTag.builder().name(normalizedTag).build()));

        if (habit.getTags().contains(tag)) {
            return null;
        }

        habit.getTags().add(tag);
        habitRepository.save(habit);

        return HabitTagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .habitId(habitId)
                .build();
    }
}
