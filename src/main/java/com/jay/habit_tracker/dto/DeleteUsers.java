package com.jay.habit_tracker.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteUsers {
    private List<String> emailsToKeep;
}
