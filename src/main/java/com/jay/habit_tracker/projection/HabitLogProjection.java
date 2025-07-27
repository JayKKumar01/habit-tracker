package com.jay.habit_tracker.projection;

import java.time.LocalDate;

public interface HabitLogProjection {
    Long getId();
    LocalDate getDate();
    boolean getCompleted();
    Long getHabitId();
}
