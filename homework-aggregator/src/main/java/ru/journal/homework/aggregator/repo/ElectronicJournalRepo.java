package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.ElectronicJournal;

import java.util.List;
import java.util.Optional;

public interface ElectronicJournalRepo extends JpaRepository<ElectronicJournal, Long> {
    List<ElectronicJournal> findByStudentId(Long studentId);
    Optional<ElectronicJournal> findByStudentIdAndTaskId(Long studentId, Long taskId);
} 