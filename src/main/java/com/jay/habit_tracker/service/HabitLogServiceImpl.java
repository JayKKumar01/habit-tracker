package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.HabitLog;
import com.jay.habit_tracker.mapper.HabitLogMapper;
import com.jay.habit_tracker.repository.HabitCustomRepository;
import com.jay.habit_tracker.repository.HabitLogRepository;
import com.jay.habit_tracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitLogServiceImpl implements HabitLogService {

    private final HabitCustomRepository habitCustomRepository;

    @Override
    public HabitLogResponse updateHabitLog(HabitLogRequest request) {
        return habitCustomRepository.upsertHabitLog(
                request.getHabitId(),
                request.getDate(),
                request.isCompleted()
        );
    }

}
