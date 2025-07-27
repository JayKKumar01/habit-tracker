package com.jay.habit_tracker.projection;

import java.time.Instant;

public interface UserProjection {
    Long getId();
    String getName();
    String getEmail();
    String getPassword();
    Instant getCreatedAt();
}
