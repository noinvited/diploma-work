package ru.journal.homework.aggregator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.domain.dto.TaskSubmissionDto;
import ru.journal.homework.aggregator.repo.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionMessageRepo submissionMessageRepo;

    @Autowired
    private StatusTaskRepo statusTaskRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private ElectronicJournalRepo electronicJournalRepo;

    @Autowired
    private LessonMessageRepo lessonMessageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    //Получает задание и пользователя, проверяет права доступа
    public TaskSubmissionDto getTaskSubmissionData(Long taskId, User user, Long studentId) {
        TaskSubmissionDto result = new TaskSubmissionDto();

        Optional<Task> taskOpt = taskRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            result.setError("Задание не найдено");
            return result;
        }

        Task task = taskOpt.get();
        Student student;
        boolean isTeacher = teacherRepo.existsByUserId(user.getId());

        if (isTeacher) {
            Teacher teacher = teacherRepo.findTeacherByUserId(user.getId());
            if (!task.getTeacher().getId().equals(teacher.getId())) {
                result.setError("Доступ запрещен");
                return result;
            }
            if (studentId == null) {
                result.setError("ID студента не указан");
                return result;
            }
            student = studentRepo.findById(studentId).orElse(null);
            if (student == null) {
                result.setError("Студент не найден");
                return result;
            }
            result.setTeacher(true);
        } else {
            Boolean studentExists = studentRepo.existsByUserId(user.getId());
            if (!studentExists) {
                result.setError("Доступ запрещен");
                return result;
            }
            student = studentRepo.findStudentByUserId(user.getId());
        }

        Submission submission = getOrCreateSubmission(task, student);
        List<SubmissionMessage> messages = getSubmissionMessages(submission);

        ElectronicJournal journal = electronicJournalRepo.findByStudentIdAndTaskId(student.getId(), task.getId()).orElse(null);

        result.setTask(task);
        result.setStudent(student);
        result.setSubmission(submission);
        result.setMessages(messages);
        result.setJournal(journal);

        return result;
    }

    //Получает существующую сдачу задания или создает новую
    public Submission getOrCreateSubmission(Task task, Student student) {
        return submissionRepo.findByStudentIdAndTaskId(student.getId(), task.getId())
                .orElseGet(() -> {
                    Submission submission = new Submission();
                    submission.setTask(task);
                    submission.setStudent(student);
                    submission.setSubmissionDate(Instant.now().plusSeconds(60*60*3));
                    submission.setLastUpdateDate(Instant.now().plusSeconds(60*60*3));
                    
                    // Устанавливаем начальный статус
                    StatusTask initialStatus = statusTaskRepo.findFirstByOrderById();
                    submission.setStatusTask(initialStatus);
                    
                    return submissionRepo.save(submission);
                });
    }

    //Получает историю сообщений для сдачи задания
    public List<SubmissionMessage> getSubmissionMessages(Submission submission) {
        return submissionMessageRepo.findBySubmissionOrderByCreatedAtAsc(submission);
    }

    public void saveMessage(Task task, Student student, String messageText, MultipartFile[] files, User author, boolean isTeacherMessage, boolean needsRevision) throws IOException {
        Submission submission = getOrCreateSubmission(task, student);
        
        // Сохраняем файлы
        String filesString = null;
        if (files != null && files.length > 0) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = saveFile(file, submission.getId(), isTeacherMessage);
                    fileNames.add(fileName);
                }
            }
            filesString = String.join(";", fileNames);
        }

        // Создаем сообщение с учетом московского времени
        SubmissionMessage message = new SubmissionMessage();
        message.setSubmission(submission);
        message.setMessageText(messageText);
        message.setFiles(filesString);
        message.setAuthor(author);
        message.setIsTeacherMessage(isTeacherMessage);
        // Сохраняем время в UTC+3 (московское время)
        message.setCreatedAt(Instant.now().plusSeconds(60*60*3));
        
        submissionMessageRepo.save(message);

        // Если требуются доработки, меняем статус
        if (needsRevision) {
            Optional<StatusTask> statusTask = statusTaskRepo.findById(4L);
            if (statusTask.isPresent()) {
                submission.setStatusTask(statusTask.get());
            }
        }

        // Обновляем дату последнего обновления сдачи
        submission.setLastUpdateDate(Instant.now().plusSeconds(60*60*3));
        submissionRepo.save(submission);
    }

    //Отправляет задание на проверку
    public void submitForReview(Task task, Student student, String messageText, MultipartFile[] files, User author) throws IOException {
        // Сохраняем сообщение
        saveMessage(task, student, messageText, files, author, false, false);
        
        // Получаем сдачу задания
        Submission submission = getOrCreateSubmission(task, student);
        
        // Меняем статус на "На проверке"
        Optional<StatusTask> statusTask = statusTaskRepo.findById(3L);
        if (statusTask.isPresent()) {
            submission.setStatusTask(statusTask.get());
            submission.setLastUpdateDate(Instant.now().plusSeconds(60*60*3));
            
            submissionRepo.save(submission);
        }
    }

    //Выставляет оценку за задание
    public void gradeSubmission(Task task, Student student, String messageText, MultipartFile[] files, User author, Integer mark) throws IOException {
        // Если сообщение пустое, добавляем автоматическое сообщение об оценке
        String finalMessageText = messageText.trim().isEmpty() ? 
            "Оценка: " + mark : 
            messageText.trim() + "\n(Оценка: " + mark + ")";
            
        // Сохраняем сообщение
        saveMessage(task, student, finalMessageText, files, author, true, false);
        
        // Получаем сдачу задания
        Submission submission = getOrCreateSubmission(task, student);
        
        // Создаем или обновляем запись в электронном журнале
        ElectronicJournal journal = electronicJournalRepo.findByStudentIdAndTaskId(student.getId(), task.getId())
                .orElse(new ElectronicJournal());
        
        journal.setStudent(student);
        journal.setTask(task);
        journal.setMark(mark);
        
        electronicJournalRepo.save(journal);
        
        // Меняем статус на "Проверено"
        Optional<StatusTask> statusTask = statusTaskRepo.findById(5L);
        if (statusTask.isPresent()) {
            submission.setStatusTask(statusTask.get());
            submission.setLastUpdateDate(Instant.now().plusSeconds(60*60*3));
            
            submissionRepo.save(submission);
        }
    }

    //Сохраняет файл и возвращает его относительный путь
    private String saveFile(MultipartFile file, Long submissionId, boolean isTeacherFile) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Создаем директорию, если её нет
            String subdir = isTeacherFile ? "teacher" : "student";
            Path submissionDir = Paths.get(uploadPath, submissionId.toString(), subdir);
            if (!Files.exists(submissionDir)) {
                Files.createDirectories(submissionDir);
            }

            String originalFilename = file.getOriginalFilename();
            Path filePath = submissionDir.resolve(originalFilename);
            
            // Если файл с таким именем уже существует, добавляем уникальный префикс
            if (Files.exists(filePath)) {
                String nameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                originalFilename = nameWithoutExt + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
                filePath = submissionDir.resolve(originalFilename);
            }

            Files.copy(file.getInputStream(), filePath);

            return submissionId + "/" + subdir + "/" + originalFilename;
        }
        return null;
    }

    public Task getTaskByLessonMessage(Long lessonMessageId) {
        Optional<LessonMessage> lessonMessage = lessonMessageRepo.findById(lessonMessageId);
        if (lessonMessage.isEmpty()) {
            return null;
        }
        return taskRepo.findByLessonMessage(lessonMessage.get());
    }

    public Map<String, Integer> getStudentGradesForTasks(User user, Task task) {
        Map<String, Integer> grades = new HashMap<>();
        Student student = studentRepo.findStudentByUserId(user.getId());
        if (student != null && task != null) {
            ElectronicJournal journal = electronicJournalRepo.findByStudentIdAndTaskId(student.getId(), task.getId())
                    .orElse(null);
            if (journal != null) {
                grades.put(task.getId().toString(), journal.getMark());
            }
        }
        return grades;
    }

    public Map<String, Submission> getSubmissionsForMessages(List<LessonMessage> messages, User user) {
        Map<String, Submission> submissions = new HashMap<>();
        Student student = studentRepo.findStudentByUserId(user.getId());
        
        if (student != null) {
            for (LessonMessage message : messages) {
                Task task = getTaskByLessonMessage(message.getId());
                if (task != null) {
                    Submission submission = getOrCreateSubmission(task, student);
                    submissions.put(message.getId().toString(), submission);
                }
            }
        }
        return submissions;
    }
}