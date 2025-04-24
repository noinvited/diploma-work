package ru.journal.homework.aggregator.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "group_discipline", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "group_discipline_group_id_discipline_id_key", columnNames = {"group_id", "discipline_id"})
})
@NoArgsConstructor
@AllArgsConstructor
public class GroupDiscipline {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_discipline_id_gen")
    @SequenceGenerator(name = "group_discipline_id_gen", sequenceName = "group_discipline_group_discipline_id_seq", allocationSize = 1)
    @Column(name = "group_discipline_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    public GroupDiscipline(Group group, Discipline discipline) {
        this.group = group;
        this.discipline = discipline;
    }
}