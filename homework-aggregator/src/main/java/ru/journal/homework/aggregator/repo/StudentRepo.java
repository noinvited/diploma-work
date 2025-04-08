package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Student;

public interface StudentRepo extends JpaRepository<Student, Long>{
    Student findStudentByUserId(Long userId);
}