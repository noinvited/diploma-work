package ru.journal.homework.aggregator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.journal.homework.aggregator.domain.Discipline;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.GroupDiscipline;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.repo.DisciplineRepo;
import ru.journal.homework.aggregator.repo.GroupDisciplineRepo;
import ru.journal.homework.aggregator.repo.GroupRepo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {
    private final GroupRepo groupRepo;
    private final DisciplineRepo disciplineRepo;
    private final GroupDisciplineRepo groupDisciplineRepo;

    public boolean updateGroup(Long groupId, List<Long> disciplineIds) {
        Group group = groupRepo.findById(groupId).orElse(null);
        List<GroupDiscipline> existingLinks = groupDisciplineRepo.findByGroupId(groupId);
        Set<Long> currentDisciplineIds = existingLinks.stream()
                .map(link -> link.getDiscipline().getId())
                .collect(Collectors.toSet());
        Set<Long> newDisciplineIds = disciplineIds != null ? new HashSet<>(disciplineIds) : new HashSet<>();

        if (currentDisciplineIds.equals(newDisciplineIds)) {
            return false;
        }

        Set<Long> disciplinesToRemove = new HashSet<>(currentDisciplineIds);
        disciplinesToRemove.removeAll(newDisciplineIds);

        Set<Long> disciplinesToAdd = new HashSet<>(newDisciplineIds);
        disciplinesToAdd.removeAll(currentDisciplineIds);

        if (!disciplinesToRemove.isEmpty()) {
            for(Long disciplineId : disciplinesToRemove) {
                groupDisciplineRepo.deleteByGroupIdAndDisciplineId(groupId, disciplineId);
            }
        }

        if (!disciplinesToAdd.isEmpty()) {
            List<Discipline> disciplines = disciplineRepo.findAllById(disciplinesToAdd);
            List<GroupDiscipline> newLinks = disciplines.stream()
                    .map(discipline -> new GroupDiscipline(group, discipline))
                    .collect(Collectors.toList());
            groupDisciplineRepo.saveAll(newLinks);
        }

        return true;
    }

    public void disciplineDelete(Long disciplineId){
        disciplineRepo.deleteById(disciplineId);
    }

    public int disciplineAdd(String disciplineName){
        if(disciplineName == null || disciplineName.isEmpty()){
            return 0;
        } else {
            if(disciplineRepo.existsByNameDiscipline(disciplineName)){
                return 1;
            } else {
                disciplineRepo.save(new Discipline(disciplineName));
                return 2;
            }
        }
    }

    public void groupDelete(Long groupId){
        groupRepo.deleteById(groupId);
    }

    public int groupAdd(String groupName){
        if(groupName == null || groupName.isEmpty()){
            return 0;
        } else {
            if(groupRepo.existsByNameGroup(groupName)){
                return 1;
            } else {
                groupRepo.save(new Group(groupName));
                return 2;
            }
        }
    }

    public List<Group> getAllGroups(){
        return groupRepo.findAll();
    }

    public Group getGroup(Long groupId){
        return groupRepo.findById(groupId).orElse(null);
    }

    public List<Discipline> getAllDiscipline(){
        return disciplineRepo.findAll();
    }

    public List<Discipline> getAllGroupDisciplines(Long groupId){
        List<GroupDiscipline> groupDisciplines = groupDisciplineRepo.findByGroupId(groupId);
        return groupDisciplines.stream().map(GroupDiscipline::getDiscipline).toList();
    }
}
