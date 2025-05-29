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

    @GetMapping("/schedule")
    @PreAuthorize("hasAuthority('USER')")
    public String schedule(Map<String, Object> model) {
        return "schedule";
    }
}
