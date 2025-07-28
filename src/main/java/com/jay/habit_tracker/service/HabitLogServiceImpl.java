package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_log.HabitLogRequest;
import com.jay.habit_tracker.dto.habit_log.HabitLogResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabitLogServiceImpl implements HabitLogService {
    private final EntityManager entityManager;

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

        return new HabitLogResponse(request.getHabitId(),request.getDate(),request.isCompleted());
    }

}
