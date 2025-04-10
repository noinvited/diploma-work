package ru.journal.homework.aggregator.utils.mapper;

import org.mapstruct.Mapper;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.dto.UserEditDto;

@Mapper(componentModel = "spring")
public interface UserEditMapper {
    UserEditDto toDto(User user);
}
