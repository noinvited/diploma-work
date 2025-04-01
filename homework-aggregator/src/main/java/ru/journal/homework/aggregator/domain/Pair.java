package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pair", schema = "public")
public class Pair implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('pair_pair_id_seq'::regclass)")
    @Column(name = "pair_id", nullable = false)
    private Long id;

    @Column(name = "start")
    private Instant start;

    @Column(name = "finish")
    private Instant finish;

}