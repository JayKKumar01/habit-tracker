package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.Profile;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.projection.ProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

    @Query(value = "SELECT id, bio, user_id AS userId FROM profiles", nativeQuery = true)
    List<ProfileProjection> getAllProjectedProfiles();

}
