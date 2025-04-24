package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "discipline", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Discipline implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('discipline_discipline_id_seq'::regclass)")
    @Column(name = "discipline_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "name_discipline", length = 50)
    @NotBlank
    private String nameDiscipline;

    public Discipline(String nameDiscipline){
        this.nameDiscipline = nameDiscipline;
    }
}