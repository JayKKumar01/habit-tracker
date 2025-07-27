package com.jay.habit_tracker.projection;

import java.time.Instant;
import java.time.LocalDate;

public interface HabitProjection {
    Long getId();
    String getTitle();
    String getDescription();
    String getFrequency();
    String getTargetDays(); // serialized as comma-separated string
    LocalDate getStartDate();
    LocalDate getEndDate();
    Instant getCreatedAt();
    Long getUserId();
}
