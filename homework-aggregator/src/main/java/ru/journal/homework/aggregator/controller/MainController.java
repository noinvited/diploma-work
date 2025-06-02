package ru.journal.homework.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.Teacher;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.Lesson;
import ru.journal.homework.aggregator.domain.LessonMessage;
import ru.journal.homework.aggregator.service.StudentService;
import ru.journal.homework.aggregator.service.TeacherService;

import java.time.Instant;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;

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
            model.addAttribute("selectedGroup", studentGroup);
            model.addAttribute("weekShift", weekShift);
            model.addAttribute("pairTimes", studentService.getAllPairs());
            model.addAttribute("days", studentService.getDatesString(weekShift));
            model.addAttribute("dates", studentService.getDates(weekShift));
            model.addAttribute("lessons", studentService.getWeekLessons(studentGroup.getId(), weekShift));
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
            model.addAttribute("weekShift", weekShift);
            model.addAttribute("pairTimes", teacherService.getAllPairs());
            model.addAttribute("days", teacherService.getDatesString(weekShift));
            model.addAttribute("dates", teacherService.getDates(weekShift));
            Map<String, Lesson> lessons = teacherService.getWeekLessons(teacher.getId(), weekShift);
            model.addAttribute("lessons", lessons);
            model.addAttribute("lessonMessages", teacherService.getWeekLessonMessages(lessons));
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
            @RequestParam(value = "deadline", required = false) Instant deadline,
            @RequestParam(value = "weekShift", required = false, defaultValue = "0") Integer weekShift,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Teacher teacher = teacherService.getTeacher(user);
            if (teacher == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Только преподаватель может добавлять сообщения к занятию");
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
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Неизвестная ошибка при добавлении сообщения");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении сообщения: " + e.getMessage());
        }

        return "redirect:/teacherSchedule?weekShift=" + weekShift;
    }
}
