package com.jay.habit_tracker.projection_debug;

import java.time.Instant;
import java.time.LocalDate;

public interface HabitProjectionDebug {
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
