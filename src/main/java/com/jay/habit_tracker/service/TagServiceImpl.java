package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.tag.TagDto;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.Tag;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.TagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final HabitRepository habitRepository;
    private final TagRepository tagRepository;
    private final EntityManager entityManager;

    @Override
    public TagDto addHabitTag(Long habitId, String name) {
        Habit habitRef = entityManager.getReference(Habit.class, habitId);
        String normalizedTag = name.trim().toLowerCase();

        Tag tag = tagRepository.findByName(normalizedTag)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(normalizedTag).build()));

        habitRef.getTags().add(tag);
        habitRepository.save(habitRef);

        return TagDto.builder()
                .id(tag.getId())
                .name(normalizedTag)
                .build();
    }
}
