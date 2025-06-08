package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Student;

import java.util.List;

public interface StudentRepo extends JpaRepository<Student, Long>{
    Student findStudentByUserId(Long userId);
    Boolean existsByStudentTicket(Long studentTicket);
    Boolean existsByUserId(Long userId);
    List<Student> findByGroupId(Long groupId);
}