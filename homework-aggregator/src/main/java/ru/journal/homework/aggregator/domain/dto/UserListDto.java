package ru.journal.homework.aggregator.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.journal.homework.aggregator.domain.helperEntity.Role;
import ru.journal.homework.aggregator.domain.helperEntity.Status;

@Data
@AllArgsConstructor
public class UserListDto {
    private Long id;
    private String username;
    private String fio;
    private Status status;
    private Role role;
    private Boolean active;
}
