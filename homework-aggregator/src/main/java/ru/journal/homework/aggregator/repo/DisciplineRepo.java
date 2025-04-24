package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Discipline;

public interface DisciplineRepo extends JpaRepository<Discipline, Long> {
    boolean existsByNameDiscipline(String name);
}
