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

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HabitLogMapper habitLogMapper;
    private final HabitCustomRepository habitCustomRepository;

    @Override
    public HabitLogResponse updateHabitLog(HabitLogRequest request) {
        return habitCustomRepository.upsertHabitLog(
                request.getHabitId(),
                request.getDate(),
                request.isCompleted()
        );

//        Habit habit = habitRepository.findById(request.getHabitId()).orElse(null);
//        if (habit == null){
//            return null;
//        }
//        HabitLog log = habitLogRepository.findByHabitIdAndDate(request.getHabitId(), request.getDate())
//                .orElseGet(() -> {
//                    HabitLog newLog = new HabitLog();
//                    newLog.setHabit(habit);
//                    newLog.setDate(request.getDate());
//                    return newLog;
//                });
//
//        log.setCompleted(request.isCompleted());
//        habitLogRepository.save(log);
//
//        return habitLogMapper.toDto(request);
    }

    @Override
    public List<HabitLogResponse> getAllLogsForHabit(Long habitId) {
        return habitLogRepository.findByHabitId(habitId).stream()
                .map(habitLogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HabitLogResponse> getAllLogsForUserId(Long userId) {
        return habitCustomRepository.findHabitLogResponsesByUserId(userId);
    }

}
