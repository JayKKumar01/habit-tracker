package com.jay.habit_tracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    private String name;
    private String email;
    private String password;
}
