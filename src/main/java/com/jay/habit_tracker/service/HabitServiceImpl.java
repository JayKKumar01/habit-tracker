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
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;
    private final HabitCustomRepository habitCustomRepository;

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
    public boolean editHabit(HabitEditRequest editRequest) {
        return habitRepository.findById(editRequest.getHabitId())
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

    @Override
    public List<HabitResponse> getHabitsByUserId(Long userId) {
        return habitCustomRepository.findHabitResponsesByUserId(userId);
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
