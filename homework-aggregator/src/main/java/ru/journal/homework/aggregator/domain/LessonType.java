package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "lesson_type", schema = "public")
public class LessonType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('lesson_type_lesson_type_id_seq'::regclass)")
    @Column(name = "lesson_type_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

}