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
@Table(name = "submission_message", schema = "public")
public class SubmissionMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_message_id_gen")
    @SequenceGenerator(name = "submission_message_id_gen", sequenceName = "submission_message_message_id_seq", allocationSize = 1)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "author_id")
    private User author;

    @Size(max = 2048)
    @NotNull
    @Column(name = "message_text", nullable = false, length = 2048)
    private String messageText;

    @Size(max = 2048)
    @Column(name = "files", length = 2048)
    private String files;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "is_teacher_message", nullable = false)
    private Boolean isTeacherMessage = false;

}