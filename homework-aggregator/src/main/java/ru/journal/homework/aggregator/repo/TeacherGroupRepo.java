package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.TeacherGroup;

import java.util.List;

public interface TeacherGroupRepo extends JpaRepository<TeacherGroup, Long> {
    @Query("SELECT tg.group FROM TeacherGroup tg WHERE tg.teacher.id = :teacherId")
    List<Group> findAllGroupsByTeacherId(@Param("teacherId") Long teacherId);
    void deleteByTeacherId(Long teacherId);
    boolean existsByTeacherIdAndGroupId(Long teacherId, Long groupId);
}
