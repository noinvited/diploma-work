package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lessons", schema = "public")
public class Lesson implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('lessons_lessons_id_seq'::regclass)")
    @Column(name = "lessons_id", nullable = false)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pair_id", nullable = false)
    private Pair pair;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Size(max = 50)
    @Column(name = "classroom", length = 50)
    private String classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_type_id")
    private LessonType lessonType;

}