package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "groups", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('groups_group_id_seq'::regclass)")
    @Column(name = "group_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotBlank
    @Column(name = "name_group", nullable = false, length = 50)
    private String nameGroup;

    public Group(String nameGroup){
        this.nameGroup = nameGroup;
    }
}