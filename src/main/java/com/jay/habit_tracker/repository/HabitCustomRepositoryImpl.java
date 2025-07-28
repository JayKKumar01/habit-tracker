package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.dto.HabitWithLogsResponse;
import com.jay.habit_tracker.enums.Frequency;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class HabitCustomRepositoryImpl implements HabitCustomRepository{

    private final EntityManager entityManager;

    @Override
    @Transactional
    public List<HabitResponse> findHabitResponsesByUserId(Long userId) {
        String sql = """
            SELECT h.id, h.title, h.description, h.frequency,
                   GROUP_CONCAT(htds.target_day) AS target_days,
                   h.start_date, h.end_date
            FROM habits h
            LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
            WHERE h.user_id = :userId
            GROUP BY h.id
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        List<HabitResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            HabitResponse dto = HabitResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .title((String) row[1])
                    .description((String) row[2])
                    .frequency(Frequency.valueOf((String) row[3]))
                    .targetDays(row[4] == null
                            ? Set.of()
                            : new HashSet<>(List.of(((String) row[4]).split(","))))
                    .startDate(((java.sql.Date) row[5]).toLocalDate())
                    .endDate(row[6] == null ? null : ((java.sql.Date) row[6]).toLocalDate())
                    .build();
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public List<HabitLogResponse> findHabitLogResponsesByUserId(Long userId) {
        String sql = """
        SELECT hl.habit_id, hl.date, hl.completed
        FROM habit_logs hl
        JOIN habits h ON hl.habit_id = h.id
        WHERE h.user_id = :userId
        ORDER BY hl.date DESC
    """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        List<HabitLogResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            HabitLogResponse dto = HabitLogResponse.builder()
                    .habitId(((Number) row[0]).longValue())
                    .date(((java.sql.Date) row[1]).toLocalDate())
                    .completed((Boolean) row[2])
                    .build();
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public HabitLogResponse upsertHabitLog(Long habitId, LocalDate date, boolean completed) {
        String sql = """
            INSERT INTO habit_logs (habit_id, date, completed)
            VALUES (:habitId, :date, :completed)
            ON DUPLICATE KEY UPDATE completed = :completed
        """;

        entityManager.createNativeQuery(sql)
                .setParameter("habitId", habitId)
                .setParameter("date", java.sql.Date.valueOf(date))
                .setParameter("completed", completed)
                .executeUpdate();

        return HabitLogResponse.builder()
                .habitId(habitId)
                .date(date)
                .completed(completed)
                .build();
    }

    @Override
    @Transactional
    public List<HabitWithLogsResponse> findHabitWithLogsByUserId(Long userId) {
        String sql = """
        SELECT h.id, h.title, h.description, h.frequency,
               GROUP_CONCAT(DISTINCT htds.target_day) AS target_days,
               h.start_date, h.end_date,
               hl.date AS log_date, hl.completed AS log_completed
        FROM habits h
        LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
        LEFT JOIN habit_logs hl ON hl.habit_id = h.id
        WHERE h.user_id = :userId
        GROUP BY h.id, hl.date, hl.completed
        ORDER BY h.id, hl.date DESC
    """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        Map<Long, HabitWithLogsResponse> habitMap = new LinkedHashMap<>(); // Preserve order

        for (Object[] row : rows) {
            Long habitId = ((Number) row[0]).longValue();

            // Create habit object if not already in map
            HabitWithLogsResponse habit = habitMap.get(habitId);
            if (habit == null) {
                habit = HabitWithLogsResponse.builder()
                        .id(habitId)
                        .title((String) row[1])
                        .description((String) row[2])
                        .frequency(Frequency.valueOf((String) row[3]))
                        .targetDays(row[4] == null
                                ? Set.of()
                                : new HashSet<>(List.of(((String) row[4]).split(","))))
                        .startDate(((java.sql.Date) row[5]).toLocalDate())
                        .endDate(row[6] == null ? null : ((java.sql.Date) row[6]).toLocalDate())
                        .logs(new ArrayList<>()) // initialize logs list
                        .build();
                habitMap.put(habitId, habit);
            }

            // Add log if present
            if (row[7] != null && row[8] != null) {
                HabitLogResponse log = HabitLogResponse.builder()
                        .habitId(habitId)
                        .date(((java.sql.Date) row[7]).toLocalDate())
                        .completed((Boolean) row[8])
                        .build();
                habit.getLogs().add(log);
            }
        }

        return new ArrayList<>(habitMap.values());
    }



}
