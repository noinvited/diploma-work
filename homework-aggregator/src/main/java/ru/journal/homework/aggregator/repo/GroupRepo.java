package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Group;

public interface GroupRepo extends JpaRepository<Group, Long> {
    boolean existsByNameGroup(String nameGroup);
}
