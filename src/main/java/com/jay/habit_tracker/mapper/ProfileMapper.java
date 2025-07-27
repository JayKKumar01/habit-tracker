package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.ProfileRequest;
import com.jay.habit_tracker.dto.ProfileResponse;
import com.jay.habit_tracker.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "user.email", target = "email")
    ProfileResponse toResponse(Profile profile);

    Profile toEntity(ProfileRequest profileRequest);
}
