package ru.journal.homework.aggregator.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.journal.homework.aggregator.domain.*;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TaskSubmissionDto {
    private Task task;
    private Student student;
    private Submission submission;
    private List<SubmissionMessage> messages;
    private String error;
    private boolean isTeacher;
    private ElectronicJournal journal;

    public boolean hasError() { return error != null && !error.isEmpty();}
}
