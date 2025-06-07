package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Submission;

import java.util.Optional;

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentIdAndTaskId(Long studentId, Long taskId);
} 