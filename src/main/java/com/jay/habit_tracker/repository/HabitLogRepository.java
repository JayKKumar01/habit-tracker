package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    // Custom method to get all HabitLogs for a given habit ID
    List<HabitLog> findByHabitId(Long habitId);

    // Get a specific log by habit ID and date
    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);

    List<HabitLog> findByHabitIdIn(List<Long> habitIds);
}
