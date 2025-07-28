package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.enums.Frequency;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HabitCustomRepository {

    private final EntityManager entityManager;

    public List<HabitResponse> findHabitResponsesByUserId(Long userId) {
        String sql = """
            SELECT h.id, h.title, h.description, h.frequency,
                   GROUP_CONCAT(htds.target_day) AS target_days,
                   h.start_date, h.end_date
            FROM habits h
            LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
            WHERE h.user_id = :userId
            GROUP BY h.id
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        List<HabitResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            HabitResponse dto = HabitResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .title((String) row[1])
                    .description((String) row[2])
                    .frequency(Frequency.valueOf((String) row[3]))
                    .targetDays(row[4] == null
                            ? Set.of()
                            : new HashSet<>(List.of(((String) row[4]).split(","))))
                    .startDate(((java.sql.Date) row[5]).toLocalDate())
                    .endDate(row[6] == null ? null : ((java.sql.Date) row[6]).toLocalDate())
                    .build();
            result.add(dto);
        }
        return result;
    }
}
