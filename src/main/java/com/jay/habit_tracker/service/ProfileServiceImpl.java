package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.profile.ProfileUpdateDto;
import com.jay.habit_tracker.entity.Profile;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.ProfileMapper;
import com.jay.habit_tracker.repository.ProfileRepository;
import com.jay.habit_tracker.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final ProfileMapper profileMapper;


    @Override
    @Transactional
    public ProfileUpdateDto saveOrUpdate(Long userId, ProfileUpdateDto updateDto) {
        // ✅ Upsert into profiles (bio)
        entityManager.createNativeQuery("""
        INSERT INTO profiles (user_id, bio)
        VALUES (:userId, :bio)
        ON DUPLICATE KEY UPDATE bio = :bio
    """)
                .setParameter("userId", userId)
                .setParameter("bio", updateDto.getBio())
                .executeUpdate();

        // ✅ Update users (name)
        entityManager.createNativeQuery("""
        UPDATE users
        SET name = :name
        WHERE id = :userId
    """)
                .setParameter("name", updateDto.getName())
                .setParameter("userId", userId)
                .executeUpdate();

        return updateDto;
    }






    @Override
    public ProfileUpdateDto getProfile(Long userId) {
        Profile profile = entityManager.createQuery("""
            SELECT p FROM Profile p
            WHERE p.user.id = :userId
        """, Profile.class)
                .setParameter("userId", userId)
                .getSingleResult(); // ← This is correct for @OneToOne

        return profileMapper.toDto(profile);
    }

}

