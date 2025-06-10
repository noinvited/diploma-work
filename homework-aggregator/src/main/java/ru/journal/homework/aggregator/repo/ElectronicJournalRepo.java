package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.journal.homework.aggregator.domain.ElectronicJournal;

import java.util.List;
import java.util.Optional;

public interface ElectronicJournalRepo extends JpaRepository<ElectronicJournal, Long> {
    @Query("SELECT ej FROM ElectronicJournal ej " +
           "LEFT JOIN FETCH ej.task t " +
           "LEFT JOIN FETCH t.lessonMessage lm " +
           "LEFT JOIN FETCH lm.lessons l " +
           "LEFT JOIN FETCH l.discipline " +
           "WHERE ej.student.id = :studentId")
    List<ElectronicJournal> findByStudentId(@Param("studentId") Long studentId);
    
    Optional<ElectronicJournal> findByStudentIdAndTaskId(Long studentId, Long taskId);
} 