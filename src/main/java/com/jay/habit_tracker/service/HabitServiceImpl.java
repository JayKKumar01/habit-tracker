package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit.*;
import com.jay.habit_tracker.dto.habit_log.HabitLogUpdateDto;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.HabitTag;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.enums.Frequency;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitRepository;
import com.jay.habit_tracker.repository.HabitTagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final EntityManager entityManager;
    private final HabitTagRepository habitTagRepository;

    @Override
    @Transactional
    public HabitResponse createHabit(Long userId, HabitRequest habitRequest) {
        Long habitId = insertHabit(userId, habitRequest);

        attachFrequencyTag(habitId, habitRequest.getFrequency());

        Set<String> tagNames = habitRequest.getTags();
        if (tagNames != null && !tagNames.isEmpty()) {
            insertTagsIfAbsent(tagNames);
            List<Long> tagIds = fetchTagIds(tagNames);
            mapTagsToHabit(habitId, tagIds);
        }

        return new HabitResponse(
                habitId,
                habitRequest.getTitle(),
                habitRequest.getDescription(),
                habitRequest.getFrequency(),
                habitRequest.getTargetDays().stream()
                        .map(String::toUpperCase)
                        .map(DayOfWeek::valueOf)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class))),
                habitRequest.getStartDate()
        );

    }

    private Long insertHabit(Long userId, HabitRequest req) {
        entityManager.createNativeQuery("""
        INSERT INTO habits (user_id, title, description, frequency, start_date, created_at, updated_at)
        VALUES (:userId, :title, :description, :frequency, :startDate, NOW(), NOW())
    """)
                .setParameter("userId", userId)
                .setParameter("title", req.getTitle())
                .setParameter("description", req.getDescription())
                .setParameter("frequency", req.getFrequency().name())
                .setParameter("startDate", req.getStartDate())
                .executeUpdate();

        BigInteger id = (BigInteger) entityManager
                .createNativeQuery("SELECT LAST_INSERT_ID()")
                .getSingleResult();

        insertTargetDays(id.longValue(), req.getTargetDays());

        return id.longValue();
    }

    private void insertTargetDays(Long habitId, Set<String> days) {
        if (days == null || days.isEmpty()) return;

        for (String day : days) {
            entityManager.createNativeQuery("""
            INSERT INTO habit_target_days (habit_id, day_of_week)
            VALUES (:habitId, :day)
        """)
                    .setParameter("habitId", habitId)
                    .setParameter("day", day)
                    .executeUpdate();
        }
    }

    private void attachFrequencyTag(Long habitId, Frequency frequency) {
        Long freqTagId = (frequency == Frequency.DAILY) ? 1L : 3L;
        entityManager.createNativeQuery("""
        INSERT INTO habit_tag_mapping (habit_id, tag_id)
        VALUES (:habitId, :tagId)
    """)
                .setParameter("habitId", habitId)
                .setParameter("tagId", freqTagId)
                .executeUpdate();
    }

    private void insertTagsIfAbsent(Set<String> tagNames) {
        for (String name : tagNames) {
            entityManager.createNativeQuery("""
            INSERT INTO habit_tags (name)
            VALUES (:name)
            ON DUPLICATE KEY UPDATE name = name
        """)
                    .setParameter("name", name)
                    .executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> fetchTagIds(Set<String> tagNames) {
        return ((List<Number>) entityManager.createNativeQuery("""
        SELECT id FROM habit_tags WHERE name IN :names
    """)
                .setParameter("names", tagNames)
                .getResultList())
                .stream()
                .map(Number::longValue)
                .toList();
    }

    private void mapTagsToHabit(Long habitId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            entityManager.createNativeQuery("""
            INSERT INTO habit_tag_mapping (habit_id, tag_id)
            VALUES (:habitId, :tagId)
        """)
                    .setParameter("habitId", habitId)
                    .setParameter("tagId", tagId)
                    .executeUpdate();
        }
    }









    @Override
    @Transactional
    public HabitUpdateDto updateHabit(HabitUpdateDto updateDto) {
        StringBuilder sql = new StringBuilder("UPDATE habits SET title = :title, description = :description");

        if (updateDto.getEndDate() != null) {
            sql.append(", end_date = :endDate");
        }

        sql.append(" WHERE id = :habitId");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("title", updateDto.getTitle());
        query.setParameter("description", updateDto.getDescription());
        query.setParameter("habitId", updateDto.getHabitId());

        if (updateDto.getEndDate() != null) {
            query.setParameter("endDate", updateDto.getEndDate());
        }

        query.executeUpdate();

        return updateDto; // ⚠️ Ensure mapper doesn't trigger user.getName() etc.
    }

    @Override
    public List<HabitWithLogsResponse> getHabitWithLogsByUserId(Long userId) {
        String sql = """
        SELECT h.id, h.title, h.description, h.frequency,
               GROUP_CONCAT(DISTINCT htds.target_day) AS target_days,
               h.start_date, h.end_date,
               hl.date AS log_date, hl.completed AS log_completed
        FROM habits h
        LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
        LEFT JOIN habit_logs hl ON hl.habit_id = h.id
        WHERE h.user_id = :userId
        GROUP BY h.id, hl.date, hl.completed
        ORDER BY h.id, hl.date DESC
    """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        Map<Long, HabitWithLogsResponse> habitMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long habitId = ((Number) row[0]).longValue();
            String targetDaysRaw = (String) row[4];

            HabitWithLogsResponse habit = habitMap.get(habitId);
            if (habit == null) {
                // ✅ Convert comma-separated string into EnumSet<DayOfWeek>
                Set<DayOfWeek> targetDays = (targetDaysRaw == null || targetDaysRaw.isBlank())
                        ? EnumSet.noneOf(DayOfWeek.class)
                        : EnumSet.copyOf(
                        Arrays.stream(targetDaysRaw.split(","))
                                .map(String::trim)
                                .map(DayOfWeek::valueOf)
                                .collect(Collectors.toSet())
                );

                habit = HabitWithLogsResponse.builder()
                        .id(habitId)
                        .title((String) row[1])
                        .description((String) row[2])
                        .frequency(Frequency.valueOf((String) row[3]))
                        .targetDays(targetDays)
                        .startDate(((java.sql.Date) row[5]).toLocalDate())
                        .endDate(row[6] == null ? null : ((java.sql.Date) row[6]).toLocalDate())
                        .build();

                habitMap.put(habitId, habit);
            }

            Object logDateObj = row[7];
            Object completedObj = row[8];

            if (logDateObj != null && completedObj != null) {
                if (habit.getLogs() == null)
                    habit.setLogs(new ArrayList<>());

                habit.getLogs().add(HabitLogUpdateDto.builder()
                        .habitId(habitId)
                        .date(((java.sql.Date) logDateObj).toLocalDate())
                        .completed((Boolean) completedObj)
                        .build());
            }
        }

// Optional: Trim memory usage
        habitMap.values().forEach(habit -> {
            List<HabitLogUpdateDto> logs = habit.getLogs();
            if (logs instanceof ArrayList<?>) {
                ((ArrayList<?>) logs).trimToSize();
            }
        });

        return new ArrayList<>(habitMap.values());
    }

    @Override
    public boolean deleteHabit(Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return true;
                })
                .orElse(false);
    }






}
