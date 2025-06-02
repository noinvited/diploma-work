package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table(name = "lesson_message", schema = "public")
public class LessonMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('lesson_message_lesson_message_id_seq'::regclass)")
    @Column(name = "lesson_message_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lessons_id", nullable = false)
    private Lesson lessons;

    @Size(max = 2048)
    @Column(name = "text_message", length = 2048)
    private String textMessage;

    @Size(max = 2048)
    @Column(name = "file")
    private String file;

    @NotNull
    @Column(name = "need_to_perform", nullable = false)
    private Boolean needToPerform = false;

    @Column(name = "deadline")
    private Instant deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_task_id")
    private StatusTask statusTask;

    public String getFormattedDeadline() {
        if (deadline == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(deadline, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return localDateTime.format(formatter);
    }
}