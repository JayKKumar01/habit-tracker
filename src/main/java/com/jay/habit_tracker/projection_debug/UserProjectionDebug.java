package com.jay.habit_tracker.projection_debug;

import java.time.Instant;

public interface UserProjectionDebug {
    Long getId();
    String getName();
    String getEmail();
    String getPassword();
    Instant getCreatedAt();
}
