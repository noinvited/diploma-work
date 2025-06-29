package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByUsername(String login);
    User findUserByActivationCode(String activationCode);
    Boolean existsByUsername(String username);
}
