package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.HabitLog;
import com.jay.habit_tracker.projection_debug.HabitLogProjectionDebug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    @Query(value = "SELECT id, date, completed, habit_id AS habitId FROM habit_logs", nativeQuery = true)
    List<HabitLogProjectionDebug> getAllProjectedLogs();


}
