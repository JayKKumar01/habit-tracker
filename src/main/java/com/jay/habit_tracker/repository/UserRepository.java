package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.projection_debug.UserProjectionDebug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByEmailNotIn(List<String> emailsToKeep);

    @Query(value = "SELECT id, name, email, password, created_at AS createdAt FROM users", nativeQuery = true)
    List<UserProjectionDebug> getAllProjectedUsers();

}
