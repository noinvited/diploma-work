package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Submission;
import ru.journal.homework.aggregator.domain.SubmissionMessage;

import java.util.List;

public interface SubmissionMessageRepo extends JpaRepository<SubmissionMessage, Long> {
    List<SubmissionMessage> findBySubmissionOrderByCreatedAtAsc(Submission submission);
} 