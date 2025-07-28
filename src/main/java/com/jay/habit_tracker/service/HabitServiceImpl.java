package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.dto.HabitWithLogsResponse;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitCustomRepository;
import com.jay.habit_tracker.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final HabitCustomRepository habitCustomRepository;
    private final EntityManager entityManager;

    @Override
    public HabitResponse createHabit(Long userId, HabitRequest habitRequest) {
        User userRef = entityManager.getReference(User.class, userId); // no DB hit

        Habit habit = habitMapper.toEntity(habitRequest);
        habit.setUser(userRef);

        Habit savedHabit = habitRepository.save(habit);
        return habitMapper.toDto(savedHabit);
    }



    @Override
    public HabitResponse editHabit(HabitEditRequest editRequest) {
        Habit habitRef = entityManager.getReference(Habit.class, editRequest.getHabitId());

        habitRef.setTitle(editRequest.getTitle());
        habitRef.setDescription(editRequest.getDescription());
        if (editRequest.getEndDate() != null) {
            habitRef.setEndDate(editRequest.getEndDate());
        }

        Habit savedHabit = habitRepository.save(habitRef);
        return habitMapper.toDto(savedHabit);
    }


    @Override
    public List<HabitWithLogsResponse> getHabitWithLogsByUserId(Long userId) {
        return habitCustomRepository.findHabitWithLogsByUserId(userId);
    }

    @Override
    public boolean deleteHabit(Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return true;
                })
                .orElse(false);
    }




}
