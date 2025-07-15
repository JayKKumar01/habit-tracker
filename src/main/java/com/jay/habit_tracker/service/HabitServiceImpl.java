package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitRequestDTO;
import com.jay.habit_tracker.dto.HabitResponseDTO;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;

    @Override
    public HabitResponseDTO createHabit(String userEmail, HabitRequestDTO habitRequestDTO) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitMapper.toEntity(habitRequestDTO);
        habit.setUser(user);

        Habit savedHabit = habitRepository.save(habit);
        return habitMapper.toDto(savedHabit);
    }

    @Override
    public List<HabitResponseDTO> getHabitsByUser(String userEmail) {
        return habitRepository.findByUserEmail(userEmail).stream()
                .map(habitMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public HabitResponseDTO getHabitByIdForUser(Long id, String email) {
        return habitRepository.findByIdAndUserEmail(id, email)
                .map(habitMapper::toDto)
                .orElse(null);
    }
    @Override
    public boolean deleteHabitForUser(Long habitId, String userEmail) {
        return habitRepository.findByIdAndUserEmail(habitId, userEmail)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return true;
                })
                .orElse(false);
    }

}
