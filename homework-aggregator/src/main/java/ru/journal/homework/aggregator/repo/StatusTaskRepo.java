package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.StatusTask;

import java.util.Optional;

public interface StatusTaskRepo extends JpaRepository<StatusTask, Long> {
    StatusTask findFirstByOrderById();
    Optional<StatusTask> findByStatus(String status);
} 