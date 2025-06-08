package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonRepo extends JpaRepository<Lesson, Long> {
    List<Lesson> findByGroupIdAndDateIn(Long groupId, List<LocalDate> dates);
    List<Lesson> findByTeacherIdAndDateIn(Long teacherId, List<LocalDate> dates);
    List<Lesson> findByGroupId(Long groupId);
    List<Lesson> findByTeacherId(Long teacherId);
    List<Lesson> findByGroupIdAndDisciplineId(Long groupId, Long disciplineId);
}