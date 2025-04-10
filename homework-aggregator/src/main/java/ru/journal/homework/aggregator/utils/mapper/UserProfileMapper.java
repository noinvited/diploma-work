package ru.journal.homework.aggregator.utils.mapper;

import org.mapstruct.Mapper;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.dto.UserProfileDto;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileDto toDto(User user);
}
