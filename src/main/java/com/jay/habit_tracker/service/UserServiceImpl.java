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
        User user = (User) entityManager
                .createNativeQuery("SELECT * FROM users WHERE email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);

        return user != null ? userMapper.toDto(user) : null;
    }


}
