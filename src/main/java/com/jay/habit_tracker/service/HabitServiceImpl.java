package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit.*;
import com.jay.habit_tracker.dto.habit_log.HabitLogDto;
import com.jay.habit_tracker.dto.tag.TagDto;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.Tag;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.enums.Frequency;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public HabitResponse createHabit(Long userId, HabitRequest habitRequest) {
        User userRef = entityManager.getReference(User.class, userId);
        Tag frequencyTag = entityManager.getReference(Tag.class,
                habitRequest.getFrequency() == Frequency.DAILY ? 1L : 3L);

        Habit habit = habitMapper.toEntity(habitRequest);
        habit.setUser(userRef);

        habit.setTags(new HashSet<>(Collections.singleton(frequencyTag)));

        Habit savedHabit = habitRepository.save(habit);

        return habitMapper.toDto(savedHabit);
    }


    @Override
    @Transactional
    public HabitUpdate updateHabit(HabitUpdate updateDto) {
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
    public List<HabitResponse> getHabitsByUserId(Long userId) {
        String sql = """
    SELECT h.id, h.title, h.description, h.frequency,
           GROUP_CONCAT(DISTINCT htds.target_day) AS target_days,
           h.start_date, h.end_date,
           hl.date AS log_date, hl.completed AS log_completed,
           GROUP_CONCAT(DISTINCT ht.id) AS tag_ids,
           GROUP_CONCAT(DISTINCT ht.name) AS tag_names
    FROM habits h
    LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
    LEFT JOIN habit_logs hl ON hl.habit_id = h.id
    LEFT JOIN habit_tag_mapping htm ON h.id = htm.habit_id
    LEFT JOIN habit_tags ht ON ht.id = htm.tag_id
    WHERE h.user_id = :userId
    GROUP BY h.id, hl.date, hl.completed
    ORDER BY h.id, hl.date DESC
""";



        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        Map<Long, HabitResponse> habitMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long habitId = ((Number) row[0]).longValue();
            String targetDaysRaw = (String) row[4];

            HabitResponse habit = habitMap.get(habitId);
            if (habit == null) {
                Set<DayOfWeek> targetDays = (targetDaysRaw == null || targetDaysRaw.isBlank())
                        ? EnumSet.noneOf(DayOfWeek.class)
                        : EnumSet.copyOf(
                        Arrays.stream(targetDaysRaw.split(","))
                                .map(String::trim)
                                .map(DayOfWeek::valueOf)
                                .collect(Collectors.toSet())
                );

                habit = HabitResponse.builder()
                        .id(habitId)
                        .title((String) row[1])
                        .description((String) row[2])
                        .frequency(Frequency.valueOf((String) row[3]))
                        .targetDays(targetDays)
                        .startDate(((java.sql.Date) row[5]).toLocalDate())
                        .endDate(row[6] == null ? null : ((java.sql.Date) row[6]).toLocalDate())
                        .logs(new ArrayList<>())
                        .tags(new ArrayList<>())
                        .build();

                habitMap.put(habitId, habit);

                // ✅ Parse and attach tags
                Object tagIdObj = row[9];
                Object tagNameObj = row[10];
                if (tagIdObj != null && tagNameObj != null) {

                    String[] tagIds = ((String) tagIdObj).split(",");
                    String[] tagNames = ((String) tagNameObj).split(",");

                    List<TagDto> tagDtos = new ArrayList<>(tagIds.length);
                    for (int i = 0; i < tagIds.length; i++) {
                        tagDtos.add(TagDto.builder()
                                .id(Long.parseLong(tagIds[i].trim()))
                                .name(tagNames[i].trim())
                                .build());
                    }

                    habit.setTags(tagDtos);
                }

            }

            Object logDateObj = row[7];
            Object completedObj = row[8];

            if (logDateObj != null && completedObj != null) {
                habit.getLogs().add(HabitLogDto.builder()
                        .habitId(habitId)
                        .date(((java.sql.Date) logDateObj).toLocalDate())
                        .completed((Boolean) completedObj)
                        .build());
            }
        }

// Optional: Trim memory usage
        habitMap.values().forEach(habit -> {
            List<HabitLogDto> logs = habit.getLogs();
            if (logs instanceof ArrayList<?>) {
                ((ArrayList<?>) logs).trimToSize();
            }

            List<TagDto> tags = habit.getTags();
            if (tags instanceof ArrayList<?>) {
                ((ArrayList<?>) tags).trimToSize();
            }
        });

        return new ArrayList<>(habitMap.values());

    }

    @Override
    public boolean deleteHabit(Long habitId) {
        Habit habitRef = entityManager.getReference(Habit.class,habitId);
        try {
            habitRepository.delete(habitRef);
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
