package ru.journal.homework.aggregator.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.journal.homework.aggregator.domain.Student;
import ru.journal.homework.aggregator.domain.Submission;
import ru.journal.homework.aggregator.domain.SubmissionMessage;
import ru.journal.homework.aggregator.domain.Task;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TaskSubmissionDto {
    private Task task;
    private Student student;
    private Submission submission;
    private List<SubmissionMessage> messages;
    private String error;

    public boolean hasError() { return error != null && !error.isEmpty();}
}
