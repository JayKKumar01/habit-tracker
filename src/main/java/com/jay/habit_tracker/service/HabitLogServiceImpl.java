package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.HabitLog;
import com.jay.habit_tracker.mapper.HabitLogMapper;
import com.jay.habit_tracker.repository.HabitCustomRepository;
import com.jay.habit_tracker.repository.HabitLogRepository;
import com.jay.habit_tracker.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitLogServiceImpl implements HabitLogService {
    private final EntityManager entityManager;
    private final HabitLogMapper habitLogMapper;

    @Override
    @Transactional
    public HabitLogResponse updateHabitLog(HabitLogRequest request) {
        String sql = """
            INSERT INTO habit_logs (habit_id, date, completed)
            VALUES (:habitId, :date, :completed)
            ON DUPLICATE KEY UPDATE completed = :completed
        """;

        entityManager.createNativeQuery(sql)
                .setParameter("habitId", request.getHabitId())
                .setParameter("date", java.sql.Date.valueOf(request.getDate()))
                .setParameter("completed", request.isCompleted())
                .executeUpdate();

        return habitLogMapper.toDto(request);
    }

}
