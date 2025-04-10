package ru.journal.homework.aggregator.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String fio;
    private LocalDate birthdate;
    private String email;
    private String status;
}
