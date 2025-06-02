package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.LessonMessage;

import java.util.List;

public interface LessonMessageRepo extends JpaRepository<LessonMessage, Long> {
    List<LessonMessage> findByLessonsId(Long lessonId);
} 