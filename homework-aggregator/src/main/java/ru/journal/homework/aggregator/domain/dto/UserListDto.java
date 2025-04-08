package ru.journal.homework.aggregator.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.journal.homework.aggregator.domain.helperEntity.Role;

@Data
@AllArgsConstructor
public class UserListDto {
    private Long id;
    private String username;
    private String fio;
    private String status;
    private Role role;
    private Boolean active;
}
