package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;

    @Override
    public HabitResponse createHabit(String userEmail, HabitRequest habitRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitMapper.toEntity(habitRequest);
        habit.setUser(user);

        Habit savedHabit = habitRepository.save(habit);
        return habitMapper.toDto(savedHabit);
    }

    @Override
    public List<HabitResponse> getHabitsByUser(String userEmail) {
        return habitRepository.findByUserEmail(userEmail).stream()
                .map(habitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean softDeleteHabitByIdForUser(Long habitId, String userEmail, LocalDate endDate) {
        return habitRepository.findByIdAndUserEmail(habitId, userEmail)
                .map(habit -> {
                    habit.setEndDate(endDate);
                    habitRepository.save(habit);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean editHabitByIdForUser(HabitEditRequest editRequest, String email) {
        return habitRepository.findByIdAndUserEmail(editRequest.getHabitId(), email)
                .map(habit -> {
                    habit.setTitle(editRequest.getTitle());
                    habit.setDescription(editRequest.getDescription());
                    LocalDate endDate = editRequest.getEndDate();
                    if (endDate != null) {
                        habit.setEndDate(endDate);
                    }
                    habitRepository.save(habit);
                    return true;
                })
                .orElse(false);
    }

}
