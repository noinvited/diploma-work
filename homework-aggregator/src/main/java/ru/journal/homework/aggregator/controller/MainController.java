package ru.journal.homework.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.domain.dto.TaskSubmissionDto;
import ru.journal.homework.aggregator.service.StudentService;
import ru.journal.homework.aggregator.service.TeacherService;
import ru.journal.homework.aggregator.service.SubmissionService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class MainController {
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubmissionService submissionService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/studentSchedule")
    @PreAuthorize("hasAuthority('USER')")
    public String studentSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "0") Integer weekShift,
            Model model
    ) {
        Group studentGroup = studentService.getStudentGroup(user);
        if (studentGroup != null) {
            Map<String, Lesson> lessons = studentService.getWeekLessons(studentGroup.getId(), weekShift);
            model.addAttribute("selectedGroup", studentGroup);
            model.addAttribute("weekShift", weekShift);
            model.addAttribute("pairTimes", studentService.getAllPairs());
            model.addAttribute("days", studentService.getDatesString(weekShift));
            model.addAttribute("dates", studentService.getDates(weekShift));
            model.addAttribute("lessons", lessons);
            model.addAttribute("lessonMessages", studentService.getWeekLessonMessages(lessons));
        } else {
            return "redirect:/teacherSchedule";
        }
        return "studentSchedule";
    }

    @GetMapping("/teacherSchedule")
    @PreAuthorize("hasAuthority('USER')")
    public String teacherSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "0") Integer weekShift,
            Model model
    ) {
        Teacher teacher = teacherService.getTeacher(user);
        if (teacher != null) {
            Map<String, Lesson> lessons = teacherService.getWeekLessons(teacher.getId(), weekShift);
            model.addAttribute("weekShift", weekShift);
            model.addAttribute("pairTimes", teacherService.getAllPairs());
            model.addAttribute("days", teacherService.getDatesString(weekShift));
            model.addAttribute("dates", teacherService.getDates(weekShift));
            model.addAttribute("lessons", lessons);
            model.addAttribute("lessonMessages", teacherService.getWeekLessonMessages(lessons));
        } else {
            return "redirect:/studentSchedule";
        }
        return "teacherSchedule";
    }

    @PostMapping("/addLessonMessage")
    @PreAuthorize("hasAuthority('USER')")
    public String addLessonMessage(
            @AuthenticationPrincipal User user,
            @RequestParam("lessonId") Long lessonId,
            @RequestParam("textMessage") String textMessage,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "needToPerform", required = false, defaultValue = "false") Boolean needToPerform,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "weekShift", required = false, defaultValue = "0") Integer weekShift,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Teacher teacher = teacherService.getTeacher(user);
            if (teacher == null) {
                return "redirect:/studentSchedule";
            }

            Lesson lesson = teacherService.findLessonById(lessonId);
            if (!lesson.getTeacher().getId().equals(teacher.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Вы можете добавлять сообщения только к своим занятиям");
                return "redirect:/teacherSchedule?weekShift=" + weekShift;
            }

            Integer result = teacherService.saveLessonMessage(user, lessonId, textMessage, files, needToPerform, deadline);
            
            switch (result) {
                case 0:
                    redirectAttributes.addFlashAttribute("successMessage", "Сообщение успешно добавлено");
                    break;
                case 1:
                    redirectAttributes.addFlashAttribute("errorMessage", "Дата сдачи должна быть позже текущего момента");
                    break;
                case 2:
                    redirectAttributes.addFlashAttribute("errorMessage", "Для обязательной работы необходимо указать срок сдачи");
                    break;
                case 3:
                    redirectAttributes.addFlashAttribute("errorMessage", "Некорректный формат даты");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Неизвестная ошибка при добавлении сообщения");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении сообщения: " + e.getMessage());
        }

        return "redirect:/teacherSchedule?weekShift=" + weekShift;
    }

    @PostMapping("/editLessonMessage")
    @PreAuthorize("hasAuthority('USER')")
    public String editLessonMessage(
            @AuthenticationPrincipal User user,
            @RequestParam("messageId") Long messageId,
            @RequestParam("textMessage") String textMessage,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "needToPerform", required = false, defaultValue = "false") Boolean needToPerform,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "weekShift", required = false, defaultValue = "0") Integer weekShift,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Teacher teacher = teacherService.getTeacher(user);
            if (teacher == null) {
                return "redirect:/studentSchedule";
            }

            Integer result = teacherService.editLessonMessage(messageId, teacher.getId(), textMessage, files, needToPerform, deadline);
            
            switch (result) {
                case 0:
                    redirectAttributes.addFlashAttribute("successMessage", "Сообщение успешно обновлено");
                    break;
                case 1:
                    redirectAttributes.addFlashAttribute("errorMessage", "Дата сдачи должна быть позже текущего момента");
                    break;
                case 2:
                    redirectAttributes.addFlashAttribute("errorMessage", "Для обязательной работы необходимо указать срок сдачи");
                    break;
                case 3:
                    redirectAttributes.addFlashAttribute("errorMessage", "Некорректный формат даты");
                    break;
                case 4:
                    redirectAttributes.addFlashAttribute("errorMessage", "Вы можете редактировать только свои сообщения");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Неизвестная ошибка при обновлении сообщения");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении сообщения: " + e.getMessage());
        }

        return "redirect:/teacherSchedule?weekShift=" + weekShift;
    }

    @PostMapping("/deleteLessonMessage")
    @PreAuthorize("hasAuthority('USER')")
    public String deleteLessonMessage(
            @AuthenticationPrincipal User user,
            @RequestParam("messageId") Long messageId,
            @RequestParam(value = "weekShift", required = false, defaultValue = "0") Integer weekShift,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Teacher teacher = teacherService.getTeacher(user);
            if (teacher == null) {
                return "redirect:/studentSchedule";
            }

            boolean deleted = teacherService.deleteLessonMessage(messageId, teacher.getId());
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Сообщение успешно удалено");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Вы можете удалять только свои сообщения");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении сообщения: " + e.getMessage());
        }

        return "redirect:/teacherSchedule?weekShift=" + weekShift;
    }

    @GetMapping("/files/{id}/{type}/{filename:.+}")
    @ResponseBody
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath, id.toString(), type, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Кодируем имя файла для корректного отображения русских символов
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                        .replace("+", "%20");

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename*=UTF-8''" + encodedFilename)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/teacherTasks")
    @PreAuthorize("hasAuthority('USER')")
    public String teacherTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long disciplineId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Boolean needToPerform,
            @RequestParam(required = false) Long statusId,
            Model model
    ) {
        Teacher teacher = teacherService.getTeacher(user);
        if (teacher != null) {
            List<Discipline> disciplines = teacherService.getTeacherDisciplines(teacher.getId());
            List<Group> groups = teacherService.getTeacherGroups(teacher.getId());
            List<StatusTask> statusTasks = teacherService.getAllStatusTasks();

            model.addAttribute("disciplines", disciplines);
            model.addAttribute("groups", groups);
            model.addAttribute("statuses", statusTasks);
            model.addAttribute("selectedDiscipline", disciplineId);
            model.addAttribute("selectedGroup", groupId);
            model.addAttribute("selectedNeedToPerform", needToPerform);
            model.addAttribute("selectedStatus", statusId);

            List<LessonMessage> messages = teacherService.getFilteredMessages(
                    teacher.getId(),
                    disciplineId,
                    groupId,
                    needToPerform,
                    statusId
            );
            model.addAttribute("messages", messages);
        } else {
            return "redirect:/studentTasks";
        }
        return "teacherTasks";
    }

    @GetMapping("/studentTasks")
    @PreAuthorize("hasAuthority('USER')")
    public String studentTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long disciplineId,
            @RequestParam(required = false) Boolean needToPerform,
            @RequestParam(required = false) Long statusId,
            Model model
    ) {
        Group studentGroup = studentService.getStudentGroup(user);
        if (studentGroup != null) {
            List<Discipline> disciplines = studentService.getAllDisciplines();
            List<StatusTask> statusTasks = studentService.getAllStatusTasks();
            model.addAttribute("disciplines", disciplines);
            model.addAttribute("statuses", statusTasks);
            model.addAttribute("selectedDiscipline", disciplineId);
            model.addAttribute("selectedNeedToPerform", needToPerform);
            model.addAttribute("selectedStatus", statusId);

            List<LessonMessage> messages = studentService.getFilteredMessages(
                    studentGroup.getId(),
                    disciplineId,
                    needToPerform,
                    statusId
            );
            model.addAttribute("messages", messages);

            Map<String, Submission> submissions = studentService.getSubmissionsForMessages(messages, user);
            model.addAttribute("submissions", submissions);
        } else {
            return "redirect:/teacherTasks";
        }
        return "studentTasks";
    }

    @GetMapping("/marks")
    @PreAuthorize("hasAuthority('USER')")
    public String getMarks(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Group studentGroup = studentService.getStudentGroup(user);
        if (studentGroup != null) {
            // Добавляем название группы
            model.addAttribute("group", studentGroup.getNameGroup());
            
            // Получаем дисциплины группы студента
            List<Discipline> disciplines = studentService.getGroupDisciplines(studentGroup.getId());
            model.addAttribute("disciplines", disciplines);
            
            // Получаем оценки студента по всем дисциплинам
            Map<Long, List<ElectronicJournal>> studentMarks = studentService.getStudentMarks(user);
            model.addAttribute("studentMarks", studentMarks != null ? studentMarks : new HashMap<>());
        } else {
            return "redirect:/teacherSchedule";
        }
        return "marks";
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('USER')")
    public String getGroups(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long selectedGroup,
            @RequestParam(required = false) Long selectedDiscipline,
            Model model
    ) {
        Teacher teacher = teacherService.getTeacher(user);
        if (teacher != null) {
            // Получаем список групп преподавателя
            List<Group> groups = teacherService.getTeacherGroups(teacher.getId());
            model.addAttribute("groups", groups);
            
            // Если выбрана конкретная группа
            if (selectedGroup != null) {
                Group group = groups.stream()
                        .filter(g -> g.getId().equals(selectedGroup))
                        .findFirst()
                        .orElse(null);

                if (group != null) {
                    model.addAttribute("selectedGroup", group);
                    
                    // Получаем список студентов группы независимо от выбора дисциплины
                    List<Student> students = teacherService.getGroupStudents(group);
                    model.addAttribute("students", students);

                    // Получаем дисциплины для группы
                    List<Discipline> groupDisciplines = teacherService.getTeacherGroupDisciplines(teacher.getId(), group.getId());
                    model.addAttribute("groupDisciplines", groupDisciplines);
                    
                    // Если выбрана дисциплина, получаем список заданий
                    if (selectedDiscipline != null) {
                        Discipline discipline = groupDisciplines.stream()
                                .filter(d -> d.getId().equals(selectedDiscipline))
                                .findFirst()
                                .orElse(null);

                        if (discipline != null) {
                            model.addAttribute("selectedDiscipline", discipline);
                            
                            // Получаем все задания для выбранной дисциплины и группы
                            List<LessonMessage> tasks = teacherService.getGroupDisciplineTasks(group, discipline);
                            
                            // Для каждого студента получаем его оценки
                            Map<String, Map<String, Integer>> studentGrades = new HashMap<>();
                            Map<String, Submission> submissions = new HashMap<>();
                            Map<Long, Task> taskEntities = new HashMap<>(); // Map для хранения Task по LessonMessage.id
                            
                            // Сначала получаем все Task для LessonMessage
                            for (LessonMessage task : tasks) {
                                Task taskEntity = teacherService.getTaskByLessonMessage(task.getId());
                                if (taskEntity != null) {
                                    taskEntities.put(task.getId(), taskEntity);
                                }
                            }
                            
                            for (Student student : students) {
                                Map<String, Integer> grades = new HashMap<>();
                                
                                for (LessonMessage task : tasks) {
                                    Task taskEntity = taskEntities.get(task.getId());
                                    if (taskEntity != null) {
                                        // Получаем оценку из электронного журнала
                                        ElectronicJournal journal = teacherService.getElectronicJournalByStudentIdAndTaskId(student.getId(), taskEntity.getId());
                                        if (journal != null) {
                                            grades.put(task.getId().toString(), journal.getMark());
                                        }
                                        
                                        // Получаем submission для студента и задания
                                        Submission submission = submissionService.getOrCreateSubmission(taskEntity, student);
                                        submissions.put(student.getId().toString() + "_" + task.getId().toString(), submission);
                                    }
                                }
                                
                                studentGrades.put(student.getId().toString(), grades);
                            }
                            
                            model.addAttribute("tasks", tasks);
                            model.addAttribute("studentGrades", studentGrades);
                            model.addAttribute("submissions", submissions);
                        }
                    }
                }
            }
        } else {
            return "redirect:/studentSchedule";
        }
        return "groups";
    }

    @GetMapping("/submit/{messageId}")
    @PreAuthorize("hasAuthority('USER')")
    public String showSubmitPage(@AuthenticationPrincipal User user,
                               @PathVariable Long messageId, 
                               @RequestParam(required = false) String returnUrl,
                               @RequestParam(required = false) Long studentId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        Task task = submissionService.getTaskByLessonMessage(messageId);
        if (task == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Задание не найдено");
            return returnUrl != null ? "redirect:" + returnUrl : "redirect:/studentTasks";
        }

        TaskSubmissionDto data = submissionService.getTaskSubmissionData(task.getId(), user, studentId);
        
        if (data.hasError()) {
            redirectAttributes.addFlashAttribute("errorMessage", data.getError());
            return returnUrl != null ? "redirect:" + returnUrl : "redirect:/studentTasks";
        }

        model.addAttribute("task", data.getTask());
        model.addAttribute("submission", data.getSubmission());
        model.addAttribute("messages", data.getMessages());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("isTeacher", data.isTeacher());
        model.addAttribute("student", data.getStudent());
        model.addAttribute("journal", data.getJournal());
        model.addAttribute("studentId", studentId);
        
        // Добавляем сообщения из flash-атрибутов
        if (model.getAttribute("errorMessage") == null && redirectAttributes.getFlashAttributes().containsKey("errorMessage")) {
            model.addAttribute("errorMessage", redirectAttributes.getFlashAttributes().get("errorMessage"));
        }
        if (model.getAttribute("successMessage") == null && redirectAttributes.getFlashAttributes().containsKey("successMessage")) {
            model.addAttribute("successMessage", redirectAttributes.getFlashAttributes().get("successMessage"));
        }

        return "submit";
    }

    @PostMapping("/submit/{messageId}")
    @PreAuthorize("hasAuthority('USER')")
    public String submitTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long messageId,
            @RequestParam String messageText,
            @RequestParam(required = false) MultipartFile[] files,
            @RequestParam String action,
            @RequestParam(required = false) String returnUrl,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer mark,
            @RequestParam(required = false) Boolean needsRevision,
            RedirectAttributes redirectAttributes
    ) {
        Task task = submissionService.getTaskByLessonMessage(messageId);
        if (task == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Задание не найдено");
            return returnUrl != null ? "redirect:" + returnUrl : "redirect:/studentTasks";
        }

        TaskSubmissionDto data = submissionService.getTaskSubmissionData(task.getId(), user, studentId);
        
        if (data.hasError()) {
            redirectAttributes.addFlashAttribute("errorMessage", data.getError());
            return returnUrl != null ? "redirect:" + returnUrl : "redirect:/studentTasks";
        }

        try {
            if (data.isTeacher()) {
                if ("grade".equals(action)) {
                    if (needsRevision != null && needsRevision) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Нельзя выставить оценку, если требуются доработки");
                        return "redirect:/submit/" + messageId + (returnUrl != null ? "?returnUrl=" + returnUrl : "") + (studentId != null ? "&studentId=" + studentId : "");
                    }
                    if (mark == null || mark < 2 || mark > 5) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Некорректная оценка");
                        return "redirect:/submit/" + messageId + (returnUrl != null ? "?returnUrl=" + returnUrl : "") + (studentId != null ? "&studentId=" + studentId : "");
                    }
                    // Выставление оценки
                    submissionService.gradeSubmission(data.getTask(), data.getStudent(), messageText, files, user, mark);
                    redirectAttributes.addFlashAttribute("successMessage", "Оценка выставлена");
                } else {
                    // Сохранение комментария преподавателя
                    submissionService.saveMessage(data.getTask(), data.getStudent(), messageText, files, user, true, needsRevision != null && needsRevision);
                    redirectAttributes.addFlashAttribute("successMessage", "Комментарий сохранен");
                }
            } else {
                if ("submit".equals(action)) {
                    // Отправка на проверку
                    submissionService.submitForReview(data.getTask(), data.getStudent(), messageText, files, user);
                    redirectAttributes.addFlashAttribute("successMessage", "Задание отправлено на проверку");
                } else {
                    // Сохранение сообщения студента
                    submissionService.saveMessage(data.getTask(), data.getStudent(), messageText, files, user, false, false);
                    redirectAttributes.addFlashAttribute("successMessage", "Сообщение сохранено");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка: " + e.getMessage());
        }

        return "redirect:/submit/" + messageId + (returnUrl != null ? "?returnUrl=" + returnUrl : "") + (studentId != null ? "&studentId=" + studentId : "");
    }
}
