package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.enums.Frequency;
import com.jay.habit_tracker.projection_debug.HabitProjectionDebug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    @Query(value = """
    SELECT
        h.id,
        h.title,
        h.description,
        h.frequency,
        GROUP_CONCAT(htds.target_day) AS targetDays,
        h.start_date AS startDate,
        h.end_date AS endDate,
        h.created_at AS createdAt,
        h.user_id AS userId
    FROM habits h
    LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
    GROUP BY h.id
""", nativeQuery = true)
    List<HabitProjectionDebug> getAllProjectedHabits();

    List<Habit> findByFrequency(Frequency frequency);
}
