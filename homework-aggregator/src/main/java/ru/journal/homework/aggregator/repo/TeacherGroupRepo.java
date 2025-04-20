package ru.journal.homework.aggregator.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.TeacherGroup;

import java.util.List;

public interface TeacherGroupRepo extends JpaRepository<TeacherGroup, Long> {
    List<Group> findAllGroupsByTeacherId(Long teacherId);
}
