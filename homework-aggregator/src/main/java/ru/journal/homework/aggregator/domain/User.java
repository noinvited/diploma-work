package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "users_login_key", columnNames = {"login"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('users_user_id_seq'::regclass)")
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "login", nullable = false, length = 50)
    private String login;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255)
    @Column(name = "activation_code")
    private String activationCode;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 127)
    @Column(name = "fio", length = 127)
    private String fio;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 50)
    @Column(name = "role", length = 50)
    private String role;

}