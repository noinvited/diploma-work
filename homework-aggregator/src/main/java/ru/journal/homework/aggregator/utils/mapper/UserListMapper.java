package ru.journal.homework.aggregator.utils.mapper;

import org.mapstruct.Mapper;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.dto.UserListDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserListMapper {
    UserListDto toDto(User user);
    List<UserListDto> toDtoList(List<User> users);
}
