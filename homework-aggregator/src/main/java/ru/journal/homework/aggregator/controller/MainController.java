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
import ru.journal.homework.aggregator.service.StudentService;
import ru.journal.homework.aggregator.service.TeacherService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;

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

    @GetMapping("/files/{lessonId}/teacher/{filename:.+}")
    @ResponseBody
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long lessonId, @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath, lessonId.toString(), "teacher", filename);
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

        } else {
            return "redirect:/teacherSchedule";
        }
        return "marks";
    }


    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('USER')")
    public String getGroups(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Teacher teacher = teacherService.getTeacher(user);
        if (teacher != null) {

        } else {
            return "redirect:/studentSchedule";
        }
        return "groups";
    }
}
