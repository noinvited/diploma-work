package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Pair;

public interface PairRepo extends JpaRepository<Pair, Long> {
}
