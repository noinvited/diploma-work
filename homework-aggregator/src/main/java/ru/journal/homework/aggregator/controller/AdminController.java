package ru.journal.homework.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.journal.homework.aggregator.domain.Pair;
import ru.journal.homework.aggregator.service.AdminService;

import java.util.List;
import java.util.Map;


@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/editDiscipline")
    public String editDiscipline(Model model) {
        model.addAttribute("disciplines", adminService.getAllDiscipline());
        return "disciplineList";
    }

    @PostMapping("/discipline/delete")
    public String disciplineDelete(
            @RequestParam Map<String, String> form,
            @RequestParam("disciplineId") Long disciplineId
    ){
        adminService.disciplineDelete(disciplineId);
        return "redirect:/editDiscipline";
    }

    @PostMapping("/discipline/add")
    public String disciplineAdd(
            @RequestParam Map<String, String> form,
            @RequestParam("nameDiscipline") String disciplineName,
            Model model
    ){
        int disciplineAddStatus = adminService.disciplineAdd(disciplineName);

        if(disciplineAddStatus == 0){
            model.addAttribute("messageType1", "danger");
            model.addAttribute("message1", "Discipline name must not be empty!");
        } else {
            if(disciplineAddStatus == 1){
                model.addAttribute("messageType1", "secondary");
                model.addAttribute("message1", "Discipline is already exist!");
            } else {
                model.addAttribute("messageType1", "success");
                model.addAttribute("message1", "Discipline successfully added!");
            }
        }

        model.addAttribute("disciplines", adminService.getAllDiscipline());
        return "disciplineList";
    }

    @GetMapping("/editGroup")
    public String editGroup(Model model) {
        model.addAttribute("groups", adminService.getAllGroups());
        return "groupList";
    }

    @PostMapping("/group/delete")
    public String groupDelete(
            @RequestParam Map<String, String> form,
            @RequestParam("groupId") Long groupId
    ){
        adminService.groupDelete(groupId);
        return "redirect:/editGroup";
    }

    @PostMapping("/group/add")
    public String groupAdd(
            @RequestParam Map<String, String> form,
            @RequestParam("nameGroup") String groupName,
            Model model
    ){
        int groupAddStatus = adminService.groupAdd(groupName);

        if(groupAddStatus == 0){
            model.addAttribute("messageType1", "danger");
            model.addAttribute("message1", "Group name must not be empty!");
        } else {
            if(groupAddStatus == 1){
                model.addAttribute("messageType1", "secondary");
                model.addAttribute("message1", "Group is already exist!");
            } else {
                model.addAttribute("messageType1", "success");
                model.addAttribute("message1", "Group successfully added!");
            }
        }

        model.addAttribute("groups", adminService.getAllGroups());
        return "groupList";
    }

    @GetMapping("group/{groupId}")
    public String groupEditForm(@PathVariable Long groupId, Model model){
        model.addAttribute("disciplines", adminService.getAllDiscipline());
        model.addAttribute("group", adminService.getGroup(groupId));
        model.addAttribute("groupDisciplines", adminService.getAllGroupDisciplines(groupId));
        return "editGroupDiscipline";
    }

    @PostMapping("/editGroupDiscipline")
    public String groupDisciplineSave(
            @RequestParam Long groupId,
            @RequestParam(required = false) List<Long> disciplineIds,
            RedirectAttributes redirectAttributes
    ){
        boolean isGroupUpdate = adminService.updateGroup(groupId, disciplineIds);
        if(isGroupUpdate){
            redirectAttributes.addFlashAttribute("messageType1", "success");
            redirectAttributes.addFlashAttribute("message1", "Group disciplines updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("messageType1", "secondary");
            redirectAttributes.addFlashAttribute("message1", "There are no changes!");
        }

        return "redirect:/group/" + groupId;
    }

    @GetMapping("editSchedule")
    public String editSchedule(@RequestParam(required = false) Long selectedGroup,
                             @RequestParam(required = false) List<Pair> pairTimes,
                             @RequestParam(required = false) List<String> days,
                             @RequestParam(required = false, defaultValue = "0") Integer weekShift,
                             Model model) {
        model.addAttribute("groups", adminService.getAllGroups());
        model.addAttribute("weekShift", weekShift);

        if (selectedGroup != null) {
            model.addAttribute("selectedGroup", adminService.getGroup(selectedGroup));
            model.addAttribute("pairTimes", adminService.getAllPairs());
            model.addAttribute("days", adminService.getDateString(weekShift));
        }

        return "editSchedule";
    }
}
