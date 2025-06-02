package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.StatusTask;
import ru.journal.homework.aggregator.domain.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {
} 