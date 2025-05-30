package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.journal.homework.aggregator.domain.Discipline;
import ru.journal.homework.aggregator.domain.TeacherDiscipline;

import java.util.List;

public interface TeacherDisciplineRepo extends JpaRepository<TeacherDiscipline, Long> {
    @Query("SELECT td.discipline FROM TeacherDiscipline td WHERE td.teacher.id = :teacherId")
    List<Discipline> findAllDisciplinesByTeacherId(@Param("teacherId") Long teacherId);
    void deleteByTeacherId(Long teacherId);
}
