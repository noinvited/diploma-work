package ru.journal.homework.aggregator.domain.dto;

import lombok.Data;

@Data
public class LessonRequestDto {
    private Long id;
    private String date;
    private Long pairId;
    private Long groupId;
    private Long disciplineId;
    private Long teacherId;
    private String classroom;
    private Long lessonTypeId;
}