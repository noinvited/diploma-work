package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Teacher;

public interface TeacherRepo extends JpaRepository<Teacher, Long> {
    Teacher findTeacherByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}
