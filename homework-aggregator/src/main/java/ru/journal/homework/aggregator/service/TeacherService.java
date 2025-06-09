package ru.journal.homework.aggregator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.repo.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private final TeacherRepo teacherRepo;
    private final PairRepo pairRepo;
    private final LessonRepo lessonRepo;
    private final LessonMessageRepo lessonMessageRepo;
    private final TaskRepo taskRepo;
    private final StatusTaskRepo statusTaskRepo;
    private final TeacherDisciplineRepo teacherDisciplineRepo;
    private final TeacherGroupRepo teacherGroupRepo;
    private final StudentRepo studentRepo;
    private final GroupDisciplineRepo groupDisciplineRepo;
    private final ElectronicJournalRepo electronicJournalRepo;

    public TeacherService(TeacherRepo teacherRepo, PairRepo pairRepo, LessonRepo lessonRepo, LessonMessageRepo lessonMessageRepo, TaskRepo taskRepo, StatusTaskRepo statusTaskRepo, TeacherDisciplineRepo teacherDisciplineRepo, TeacherGroupRepo teacherGroupRepo, StudentRepo studentRepo, GroupDisciplineRepo groupDisciplineRepo, ElectronicJournalRepo electronicJournalRepo) {
        this.teacherRepo = teacherRepo;
        this.pairRepo = pairRepo;
        this.lessonRepo = lessonRepo;
        this.lessonMessageRepo = lessonMessageRepo;
        this.taskRepo = taskRepo;
        this.statusTaskRepo = statusTaskRepo;
        this.teacherDisciplineRepo = teacherDisciplineRepo;
        this.teacherGroupRepo = teacherGroupRepo;
        this.studentRepo = studentRepo;
        this.groupDisciplineRepo = groupDisciplineRepo;
        this.electronicJournalRepo = electronicJournalRepo;
    }

    @Value("${upload.path}")
    private String uploadPath;

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

    public Map<String, Lesson> getWeekLessons(Long teacherId, Integer weekShift) {
        List<LocalDate> dates = getDates(weekShift);
        List<Lesson> lessons = lessonRepo.findByTeacherIdAndDateIn(teacherId, dates);

        Map<String, Lesson> lessonMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Lesson lesson : lessons) {
            String key = lesson.getDate().format(formatter) + "_" + lesson.getPair().getId();
            lessonMap.put(key, lesson);
        }

        return lessonMap;
    }

    public Map<String, LessonMessage> getWeekLessonMessages(Map<String, Lesson> lessons) {
        Map<String, LessonMessage> messagesMap = new HashMap<>();
        
        for (Map.Entry<String, Lesson> entry : lessons.entrySet()) {
            List<LessonMessage> messages = lessonMessageRepo.findByLessonsId(entry.getValue().getId());
            if (!messages.isEmpty()) {
                messagesMap.put(entry.getKey(), messages.get(0));
            }
        }
        
        return messagesMap;
    }

    public List<Pair> getAllPairs() {
        return pairRepo.findAll();
    }

    public Teacher getTeacher(User user){
        return teacherRepo.findTeacherByUserId(user.getId());
    }

    public Integer saveLessonMessage(User user, Long lessonId, String textMessage,
                                     MultipartFile[] files, Boolean needToPerform,
                                     String deadlineStr) throws IOException {

        // Преобразование строки даты в Instant
        Instant deadline = null;
        if (needToPerform) {
            if (deadlineStr == null || deadlineStr.isEmpty()) {
                return 2; // Ошибка: для обязательной работы необходимо указать срок сдачи
            }
        }

        if(deadlineStr != null && !deadlineStr.isEmpty()){
            try {
                deadline = LocalDateTime.parse(deadlineStr).atZone(ZoneId.systemDefault()).toInstant();
                if (!deadline.isAfter(Instant.now())) {
                    return 1; // Ошибка: дата сдачи должна быть позже текущего момента
                }
            } catch (Exception e) {
                return 3; // Ошибка: некорректный формат даты
            }
        }

        LessonMessage message = new LessonMessage();
        message.setLessons(findLessonById(lessonId));
        message.setTextMessage(textMessage);
        message.setNeedToPerform(needToPerform);

        if (deadline != null) {
            message.setDeadline(deadline);
        }

        if (files != null && files.length > 0) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = saveFile(file, lessonId);
                    fileNames.add(fileName);
                }
            }
            message.setFile(String.join(";", fileNames));
        }

        // Устанавливаем начальный статус для обязательного задания
        if (needToPerform) {
            message.setStatusTask(statusTaskRepo.findFirstByOrderById());
        }

        lessonMessageRepo.save(message);

        if(needToPerform){
            Task task = new Task();
            task.setTask(textMessage);
            task.setLessonMessage(message);
            
            Lesson lesson = findLessonById(lessonId);
            task.setTeacher(lesson.getTeacher());
            task.setDiscipline(lesson.getDiscipline());
            
            taskRepo.save(task);
        }
        return 0;
    }

    public Integer editLessonMessage(Long messageId, Long teacherId, String textMessage,
                                     MultipartFile[] files, Boolean needToPerform,
                                     String deadline) throws IOException {
        Optional<LessonMessage> messageOpt = lessonMessageRepo.findById(messageId);
        if (messageOpt.isEmpty()) {
            return 4;
        }

        LessonMessage message = messageOpt.get();
        if (!message.getLessons().getTeacher().getId().equals(teacherId)) {
            return 4;
        }

        Instant deadlineInstant = null;
        if (needToPerform) {
            if (deadline == null || deadline.isEmpty()) {
                return 2; // Ошибка: для обязательной работы необходимо указать срок сдачи
            }
        }

        if(deadline != null && !deadline.isEmpty()){
            try {
                deadlineInstant = LocalDateTime.parse(deadline).atZone(ZoneId.systemDefault()).toInstant();
                if (!deadlineInstant.isAfter(Instant.now())) {
                    return 1; // Ошибка: дата сдачи должна быть позже текущего момента
                }
            } catch (Exception e) {
                return 3; // Ошибка: некорректный формат даты
            }
        }

        message.setTextMessage(textMessage);
        message.setNeedToPerform(needToPerform);

        if (deadlineInstant != null) {
            message.setDeadline(deadlineInstant);
        }

        if (files != null && files.length > 0) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {

                    String fileName = saveFile(file, message.getLessons().getId());
                    fileNames.add(fileName);
                }
            }
            message.setFile(String.join(";", fileNames));
        }

        if (needToPerform) {
            message.setStatusTask(statusTaskRepo.findFirstByOrderById());
        }

        lessonMessageRepo.save(message);

        if(needToPerform){
            Task task = getTaskByLessonMessage(messageId);
            if (task == null) {
                task = new Task();
                task.setLessonMessage(message);
                task.setTeacher(message.getLessons().getTeacher());
                task.setDiscipline(message.getLessons().getDiscipline());
            }
            task.setTask(textMessage);

            taskRepo.save(task);
        }

        return 0;
    }

    public Lesson findLessonById(Long id) {
        return lessonRepo.findById(id).get();
    }

    public String saveFile(MultipartFile file, Long lessonId) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Создаем директорию, если её нет
            Path lessonDir = Paths.get(uploadPath, lessonId.toString(), "teacher");
            if (!Files.exists(lessonDir)) {
                Files.createDirectories(lessonDir);
            }

            String originalFilename = file.getOriginalFilename();
            Path filePath = lessonDir.resolve(originalFilename);
            
            // Если файл с таким именем уже существует, добавляем уникальный префикс
            if (Files.exists(filePath)) {
                String nameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                originalFilename = nameWithoutExt + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
                filePath = lessonDir.resolve(originalFilename);
            }

            Files.copy(file.getInputStream(), filePath);

            return lessonId + "/teacher/" + originalFilename;
        }
        return null;
    }

    @Transactional
    public boolean deleteLessonMessage(Long messageId, Long teacherId) {
        Optional<LessonMessage> messageOpt = lessonMessageRepo.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }

        LessonMessage message = messageOpt.get();
        if (!message.getLessons().getTeacher().getId().equals(teacherId)) {
            return false;
        }

        // Удаляем связанное задание, если оно есть
        Task task = getTaskByLessonMessage(messageId);
        if (task != null) {
            taskRepo.delete(task);
        }

        // Удаляем файлы, если они есть
        if (message.getFile() != null && !message.getFile().isEmpty()) {
            String[] files = message.getFile().split(";");
            for (String filePath : files) {
                try {
                    Path path = Paths.get(uploadPath, filePath);
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    // Логируем ошибку, но продолжаем удаление
                    e.printStackTrace();
                }
            }
        }

        lessonMessageRepo.delete(message);
        return true;
    }

    public List<Discipline> getTeacherDisciplines(Long teacherId) {
        return teacherDisciplineRepo.findAllDisciplinesByTeacherId(teacherId);
    }

    public List<Group> getTeacherGroups(Long teacherId) {
        return teacherGroupRepo.findAllGroupsByTeacherId(teacherId);
    }

    public List<Student> getGroupStudents(Group group) {
        return studentRepo.findByGroupId(group.getId());
    }

    public List<StatusTask> getAllStatusTasks() {
        return statusTaskRepo.findAll();
    }

    public Task getTaskByLessonMessage(Long lessonMessageId){
        return taskRepo.findByLessonMessage(lessonMessageRepo.findById(lessonMessageId).get());
    }

    public ElectronicJournal getElectronicJournalByStudentIdAndTaskId(Long studentId, Long taskId){
        return electronicJournalRepo.findByStudentIdAndTaskId(studentId, taskId).orElse(null);
    }

    public List<LessonMessage> getFilteredMessages(
            Long teacherId,
            Long disciplineId,
            Long groupId,
            Boolean needToPerform,
            Long statusId
    ) {
        List<Lesson> lessons = lessonRepo.findByTeacherId(teacherId);

        if (disciplineId != null) {
            lessons = lessons.stream()
                    .filter(lesson -> lesson.getDiscipline().getId().equals(disciplineId))
                    .collect(Collectors.toList());
        }

        if (groupId != null) {
            lessons = lessons.stream()
                    .filter(lesson -> lesson.getGroup().getId().equals(groupId))
                    .collect(Collectors.toList());
        }

        List<LessonMessage> allMessages = new ArrayList<>();
        for (Lesson lesson : lessons) {
            List<LessonMessage> messages = lessonMessageRepo.findByLessonsId(lesson.getId());
            allMessages.addAll(messages);
        }

        if (needToPerform != null) {
            allMessages = allMessages.stream()
                    .filter(msg -> msg.getNeedToPerform() == needToPerform)
                    .collect(Collectors.toList());
        }

        if (statusId != null) {
            allMessages = allMessages.stream()
                    .filter(msg -> {
                        if (msg.getStatusTask() == null) {
                            return false;
                        }
                        return msg.getStatusTask().getId().equals(statusId);
                    })
                    .collect(Collectors.toList());
        }

        allMessages.sort((m1, m2) -> m2.getLessons().getDate().compareTo(m1.getLessons().getDate()));

        return allMessages;
    }

    public List<Discipline> getTeacherGroupDisciplines(Long teacherId, Long groupId) {
        // Получаем все дисциплины преподавателя
        List<Discipline> teacherDisciplines = teacherDisciplineRepo.findAllDisciplinesByTeacherId(teacherId);
        // Получаем все дисциплины группы
        List<GroupDiscipline> groupDisciplines = groupDisciplineRepo.findByGroupId(groupId);
        Set<Long> groupDisciplineIds = groupDisciplines.stream()
                .map(gd -> gd.getDiscipline().getId())
                .collect(Collectors.toSet());
        
        // Оставляем только те дисциплины, которые есть и у преподавателя, и у группы
        return teacherDisciplines.stream()
                .filter(d -> groupDisciplineIds.contains(d.getId()))
                .collect(Collectors.toList());
    }

    public List<LessonMessage> getGroupDisciplineTasks(Group group, Discipline discipline) {
        // Получаем все занятия для группы по дисциплине
        List<Lesson> lessons = lessonRepo.findByGroupIdAndDisciplineId(group.getId(), discipline.getId());
        
        // Получаем все сообщения для этих занятий и сортируем по дате
        return lessons.stream()
                .flatMap(lesson -> lessonMessageRepo.findByLessonsId(lesson.getId()).stream())
                .filter(message -> message.getNeedToPerform()) // Только задания, которые нужно выполнить
                .sorted(Comparator.comparing(message -> message.getLessons().getDate()))
                .collect(Collectors.toList());
    }
}
