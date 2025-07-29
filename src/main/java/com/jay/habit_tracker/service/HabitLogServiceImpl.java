package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_log.HabitLogDto;
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
    public HabitLogDto updateHabitLog(HabitLogDto updateDto) {
        String sql = """
            INSERT INTO habit_logs (habit_id, date, completed)
            VALUES (:habitId, :date, :completed)
            ON DUPLICATE KEY UPDATE completed = :completed
        """;

        entityManager.createNativeQuery(sql)
                .setParameter("habitId", updateDto.getHabitId())
                .setParameter("date", java.sql.Date.valueOf(updateDto.getDate()))
                .setParameter("completed", updateDto.isCompleted())
                .executeUpdate();

        return updateDto;
    }

}
