package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.LessonType;

public interface LessonTypeRepo extends JpaRepository<LessonType, Long> {
} 