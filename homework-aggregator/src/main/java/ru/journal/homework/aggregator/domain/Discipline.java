package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "discipline", schema = "public")
public class Discipline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('discipline_discipline_id_seq'::regclass)")
    @Column(name = "discipline_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "name_discipline", length = 50)
    private String nameDiscipline;

}