package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserEmail(String email);
    // Finds habit by ID AND the associated user's email
    Optional<Habit> findByIdAndUserEmail(Long id, String email);

}
