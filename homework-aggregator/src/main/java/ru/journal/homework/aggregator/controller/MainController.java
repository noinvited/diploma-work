package ru.journal.homework.aggregator.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class MainController {
    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/studentSchedule")
    @PreAuthorize("hasAuthority('USER')")
    public String studentSchedule(Map<String, Object> model) {
        return "studentSchedule";
    }

    @GetMapping("/teacherSchedule")
    @PreAuthorize("hasAuthority('USER')")
    public String teacherSchedule(Map<String, Object> model) {
        return "teacherSchedule";
    }
}
