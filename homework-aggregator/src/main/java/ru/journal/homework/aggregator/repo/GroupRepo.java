package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.journal.homework.aggregator.domain.Group;

public interface GroupRepo extends JpaRepository<Group, Long> {
    boolean existsByNameGroup(String nameGroup);

    @Query("SELECT s.group FROM Student s WHERE s.user.id = :userId")
    Group findGroupByStudentUserId(@Param("userId") Long userId);
}
