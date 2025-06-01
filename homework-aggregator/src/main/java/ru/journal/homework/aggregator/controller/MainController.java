package ru.journal.homework.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.Teacher;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.service.StudentService;
import ru.journal.homework.aggregator.service.TeacherService;

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
            model.addAttribute("lessons", teacherService.getWeekLessons(teacher.getId(), weekShift));
        }
        return "teacherSchedule";
    }
}
