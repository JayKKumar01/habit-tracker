package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitRequestDTO;
import com.jay.habit_tracker.dto.HabitResponseDTO;

import java.util.List;

public interface HabitService {
    HabitResponseDTO createHabit(String userEmail, HabitRequestDTO habitRequestDTO);
    List<HabitResponseDTO> getHabitsByUser(String userEmail);
    boolean deleteHabitForUser(Long id, String tokenEmail);
    HabitResponseDTO getHabitByIdForUser(Long id, String tokenEmail);
}
