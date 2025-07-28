package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.entity.HabitTag;
import com.jay.habit_tracker.projection_debug.HabitTagProjectionDebug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HabitTagRepository extends JpaRepository<HabitTag, Long> {
    Optional<HabitTag> findByName(String normalizedTag);

    @Query(value = """
    SELECT ht.id AS id, ht.name AS name, htm.habit_id AS habitId
    FROM habit_tags ht
    LEFT JOIN habit_tag_mapping htm ON ht.id = htm.tag_id
""", nativeQuery = true)
    List<HabitTagProjectionDebug> getAllProjectedTags();


    List<HabitTag> findByNameIn(Set<String> requestedTagNames);
}
