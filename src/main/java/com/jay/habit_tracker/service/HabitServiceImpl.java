package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.*;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.HabitMapper;
import com.jay.habit_tracker.repository.HabitCustomRepository;
import com.jay.habit_tracker.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final HabitCustomRepository habitCustomRepository;
    private final EntityManager entityManager;

    @Override
    public HabitResponse createHabit(Long userId, HabitRequest habitRequest) {
        User userRef = entityManager.getReference(User.class, userId); // no DB hit

        Habit habit = habitMapper.toEntity(habitRequest);
        habit.setUser(userRef);

        Habit savedHabit = habitRepository.save(habit);
        return habitMapper.toDto(savedHabit);
    }



    @Override
    @Transactional
    public HabitEditResponse editHabit(HabitEditRequest editRequest) {
        StringBuilder sql = new StringBuilder("UPDATE habits SET title = :title, description = :description");

        if (editRequest.getEndDate() != null) {
            sql.append(", end_date = :endDate");
        }

        sql.append(" WHERE id = :habitId");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("title", editRequest.getTitle());
        query.setParameter("description", editRequest.getDescription());
        query.setParameter("habitId", editRequest.getHabitId());

        if (editRequest.getEndDate() != null) {
            query.setParameter("endDate", editRequest.getEndDate());
        }

        query.executeUpdate();

        return habitMapper.toEditResponse(editRequest); // ⚠️ Ensure mapper doesn't trigger user.getName() etc.
    }






    @Override
    public List<HabitWithLogsResponse> getHabitWithLogsByUserId(Long userId) {
        return habitCustomRepository.findHabitWithLogsByUserId(userId);
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
