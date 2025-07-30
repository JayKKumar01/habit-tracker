package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.tag.TagDto;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.Tag;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final HabitRepository habitRepository;
    private final TagRepository tagRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public TagDto addHabitTag(Long habitId, String name) {
        String normalizedTag = name.trim().toLowerCase();

        Tag tag;
        try {
            tag = entityManager.createQuery(
                            "SELECT t FROM Tag t WHERE t.name = :name", Tag.class)
                    .setParameter("name", normalizedTag)
                    .getSingleResult();
        } catch (NoResultException e) {
            tag = Tag.builder().name(normalizedTag).build();
            entityManager.persist(tag);
        }

        // Step 3: Insert into habit_tag_mapping only if not already mapped
        String insertSql = """
        INSERT INTO habit_tag_mapping (habit_id, tag_id)
        SELECT :habitId, :tagId
        WHERE NOT EXISTS (
            SELECT 1 FROM habit_tag_mapping WHERE habit_id = :habitId AND tag_id = :tagId
        )
        """;

        entityManager.createNativeQuery(insertSql)
                .setParameter("habitId", habitId)
                .setParameter("tagId", tag.getId())
                .executeUpdate();

        // Step 4: Return the DTO
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    @Override
    @Transactional
    public void removeHabitTag(Long habitId, Long tagId) {
        String deleteSql = """
        DELETE FROM habit_tag_mapping
        WHERE habit_id = :habitId AND tag_id = :tagId
    """;

        entityManager.createNativeQuery(deleteSql)
                .setParameter("habitId", habitId)
                .setParameter("tagId", tagId)
                .executeUpdate();
    }

}
