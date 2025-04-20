package ru.journal.homework.aggregator.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
   @GetMapping("/editGroupDiscipline")
    public String userEditForm(Model model){

        return "editGroupDiscipline";
    }
}
