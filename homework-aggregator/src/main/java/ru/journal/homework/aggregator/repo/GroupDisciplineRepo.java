package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.GroupDiscipline;

import java.util.List;

public interface GroupDisciplineRepo extends JpaRepository<GroupDiscipline, Long> {
    List<GroupDiscipline> findByGroupId(Long groupId);
    void deleteByGroupIdAndDisciplineId(Long groupId, Long disciplineId);
}
