package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "submission", schema = "public")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_id_gen")
    @SequenceGenerator(name = "submission_id_gen", sequenceName = "submission_submission_id_seq", allocationSize = 1)
    @Column(name = "submission_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "status_task_id")
    private StatusTask statusTask;

    @NotNull
    @Column(name = "submission_date", nullable = false)
    private Instant submissionDate;

    @NotNull
    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @Size(max = 2048)
    @Column(name = "files", length = 2048)
    private String files;

}