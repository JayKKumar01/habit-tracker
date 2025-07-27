package com.jay.habit_tracker.projection_debug;

import java.time.LocalDate;

public interface HabitLogProjectionDebug {
    Long getId();
    LocalDate getDate();
    boolean getCompleted();
    Long getHabitId();
}
