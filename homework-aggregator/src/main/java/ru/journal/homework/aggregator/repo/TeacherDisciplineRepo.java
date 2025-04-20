package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Discipline;
import ru.journal.homework.aggregator.domain.TeacherDiscipline;

import java.util.List;

public interface TeacherDisciplineRepo extends JpaRepository<TeacherDiscipline, Long> {
    List<Discipline> findAllDisciplinesByTeacherId(Long teacherId);
}
