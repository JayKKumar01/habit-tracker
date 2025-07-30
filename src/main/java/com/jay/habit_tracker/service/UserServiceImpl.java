package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.user.UserResponse;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.UserMapper;
import com.jay.habit_tracker.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public UserResponse getUserByEmail(String email) {
        Object[] row = (Object[]) entityManager
                .createNativeQuery("SELECT id, name, email, created_at FROM users WHERE email = :email")
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (row == null) return null;

        return UserResponse.builder()
                .id(((Number) row[0]).longValue())
                .name((String) row[1])
                .email((String) row[2])
                .createdAt(((java.sql.Timestamp) row[3]).toInstant())
                .build();
    }



}
