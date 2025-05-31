package ru.journal.homework.aggregator.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.domain.dto.LessonRequestDto;
import ru.journal.homework.aggregator.repo.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {
    private final GroupRepo groupRepo;
    private final DisciplineRepo disciplineRepo;
    private final GroupDisciplineRepo groupDisciplineRepo;
    private final PairRepo pairRepo;
    private final LessonRepo lessonRepo;
    private final LessonTypeRepo lessonTypeRepo;
    private final TeacherDisciplineRepo teacherDisciplineRepo;
    private final TeacherRepo teacherRepo;

    @Transactional
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

    public List<Pair> getAllPairs(){
        return pairRepo.findAll();
    }

    public List<String> getDatesString(Integer shift){
        LocalDate today = LocalDate.now();

        // Находим понедельник текущей недели
        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        // Применяем смещение в неделях
        monday = monday.plusWeeks(shift);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("d ")
                .appendText(ChronoField.MONTH_OF_YEAR, new HashMap<Long, String>() {{
                    put(1L, "января");
                    put(2L, "февраля");
                    put(3L, "марта");
                    put(4L, "апреля");
                    put(5L, "мая");
                    put(6L, "июня");
                    put(7L, "июля");
                    put(8L, "августа");
                    put(9L, "сентября");
                    put(10L, "октября");
                    put(11L, "ноября");
                    put(12L, "декабря");
                }})
                .toFormatter(new Locale("ru"));

        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            LocalDate date = monday.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru"));
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
            String formattedDate = date.format(formatter);
            dates.add(dayName + ", " + formattedDate);
        }

        return dates;
    }

    public List<LocalDate> getDates(Integer shift){
        LocalDate today = LocalDate.now();

        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        monday = monday.plusWeeks(shift);

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            dates.add(monday.plusDays(i));
        }

        return dates;
    }

    public Map<String, Lesson> getWeekLessons(Long groupId, Integer weekShift) {
        List<LocalDate> dates = getDates(weekShift);
        List<Lesson> lessons = lessonRepo.findByGroupIdAndDateIn(groupId, dates);
        
        Map<String, Lesson> lessonMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Lesson lesson : lessons) {
            String key = lesson.getDate().format(formatter) + "_" + lesson.getPair().getId();
            lessonMap.put(key, lesson);
        }
        
        return lessonMap;
    }

    public List<LessonType> getAllLessonTypes() {
        return lessonTypeRepo.findAll();
    }

    public List<Teacher> getTeachersByDiscipline(Long disciplineId) {
        return teacherDisciplineRepo.findTeachersByDisciplineId(disciplineId);
    }

    @Transactional
    public void createLesson(LessonRequestDto request) {
        Lesson lesson = new Lesson();
        
        // Установка даты
        lesson.setDate(LocalDate.parse(request.getDate()));
        
        // Установка номера пары
        lesson.setPair(pairRepo.findById(request.getPairId())
                .orElseThrow(() -> new IllegalArgumentException("Pair not found")));
        
        // Установка группы
        lesson.setGroup(groupRepo.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found")));
        
        // Установка преподавателя
        lesson.setTeacher(teacherRepo.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found")));
        
        // Установка дисциплины
        lesson.setDiscipline(disciplineRepo.findById(request.getDisciplineId())
                .orElseThrow(() -> new IllegalArgumentException("Discipline not found")));
        
        // Установка аудитории
        lesson.setClassroom(request.getClassroom());
        
        // Установка типа занятия
        lesson.setLessonType(lessonTypeRepo.findById(request.getLessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found")));

        // Сохранение пары
        lessonRepo.save(lesson);
    }

    @Transactional
    public void updateLesson(LessonRequestDto request) {
        Lesson lesson = lessonRepo.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        // Установка даты
        lesson.setDate(LocalDate.parse(request.getDate()));

        // Установка номера пары
        lesson.setPair(pairRepo.findById(request.getPairId())
                .orElseThrow(() -> new IllegalArgumentException("Pair not found")));

        // Установка группы
        lesson.setGroup(groupRepo.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found")));

        // Установка преподавателя
        lesson.setTeacher(teacherRepo.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found")));

        // Установка дисциплины
        lesson.setDiscipline(disciplineRepo.findById(request.getDisciplineId())
                .orElseThrow(() -> new IllegalArgumentException("Discipline not found")));

        // Установка аудитории
        lesson.setClassroom(request.getClassroom());

        // Установка типа занятия
        lesson.setLessonType(lessonTypeRepo.findById(request.getLessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found")));

        // Сохранение обновленной пары
        lessonRepo.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long id) {
        if (!lessonRepo.existsById(id)) {
            throw new IllegalArgumentException("Lesson not found");
        }
        lessonRepo.deleteById(id);
    }
}
