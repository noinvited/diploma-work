package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "status_task", schema = "public")
public class StatusTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('status_task_status_task_id_seq'::regclass)")
    @Column(name = "status_task_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

}