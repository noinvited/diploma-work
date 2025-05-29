package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.GroupDiscipline;

import java.util.List;

public interface GroupDisciplineRepo extends JpaRepository<GroupDiscipline, Long> {
    List<GroupDiscipline> findByGroupId(Long groupId);

    @Modifying
    @Query("DELETE FROM GroupDiscipline gd WHERE gd.group.id = :groupId AND gd.discipline.id = :disciplineId")
    void deleteByGroupIdAndDisciplineId(Long groupId, Long disciplineId);

    @Query("SELECT DISTINCT g FROM Group g JOIN GroupDiscipline gd on g = gd.group WHERE gd.discipline.id IN :disciplineIds")
    List<Group> findAllGroupsByDisciplineIds(List<Long> disciplineIds);
}
