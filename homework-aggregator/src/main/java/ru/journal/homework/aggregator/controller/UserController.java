package ru.journal.homework.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.journal.homework.aggregator.domain.Group;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.repo.TeacherDisciplineRepo;
import ru.journal.homework.aggregator.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TeacherDisciplineRepo teacherDisciplineRepo;

    @GetMapping("profile")
    String profile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", userService.getUserProfileDto(user));
        model.addAttribute("group", userService.getGroupNameByStudentUser(user));
        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String email,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) LocalDate birthdate,
            Model model
    ){
        int isEmailUpdate = userService.updateEmail(user, email);
        int isPasswordUpdate = userService.updatePassword(user, oldPassword, newPassword);
        int isFioUpdate = userService.updateFio(user, fio);
        int isBirthdateUpdate = userService.updateBirthdate(user, birthdate);

        if(isPasswordUpdate == 1){
            model.addAttribute("messageType1", "danger");
            model.addAttribute("message1", "Wrong password!");
        } else {
            if(isPasswordUpdate == 2){
                model.addAttribute("messageType1", "success");
                model.addAttribute("message1", "Password successfully updated! You need to log in again to apply the changes.");
            }
        }

        if(isEmailUpdate == 1){
            model.addAttribute("messageType2", "danger");
            model.addAttribute("message2", "Email is not valid!");
        } else {
            if(isEmailUpdate == 2){
                model.addAttribute("messageType2", "success");
                model.addAttribute("message2", "Email successfully updated!");
            }
        }

        if(isFioUpdate == 1){
            model.addAttribute("messageType3", "success");
            model.addAttribute("message3", "Fio successfully updated!");
        }

        if(isBirthdateUpdate == 1){
            model.addAttribute("messageType4", "danger");
            model.addAttribute("message4", "Birthdate must be in past!");
        } else {
            if (isBirthdateUpdate == 2) {
                model.addAttribute("messageType4", "success");
                model.addAttribute("message4", "Birthdate successfully updated!");
            }
        }

        if(isEmailUpdate == 0 && isPasswordUpdate == 0 && isFioUpdate == 0 && isBirthdateUpdate == 0){
            model.addAttribute("messageType1", "secondary");
            model.addAttribute("message1", "There are no changes!");
        }
        model.addAttribute("user", userService.getUserProfileDto(user));
        model.addAttribute("group", userService.getGroupNameByStudentUser(user));
        return "profile";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUserListDto());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete")
    public String userDelete(
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ){
        userService.userDelete(user);
        return "redirect:/user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", userService.getUserEditDto(user));
        model.addAttribute("studentGroup", userService.getGroupByStudentUser(user));
        model.addAttribute("studentTicket", userService.getStudentTicketByUser(user));
        model.addAttribute("groups", userService.getAllGroups());
        model.addAttribute("disciplines", userService.getAllDiscipline());
        model.addAttribute("teacherGroups", userService.getGroupsNameByTeacherUser(user));
        model.addAttribute("teacherDisciplines", userService.getDisciplinesNameByTeacherUser(user));
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}")
    public String userSave(
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user,
            @RequestParam String username,
            @RequestParam String role,
            @RequestParam(required = false) String statusUser,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Long group,
            @RequestParam(required = false) List<Long> disciplines,
            @RequestParam(required = false) List<Long> teacherGroups,
            RedirectAttributes redirectAttributes
    ){
        int isUserUpdate = userService.updateUser(user, username, form, role, statusUser, studentId, group, disciplines, teacherGroups);

        if(isUserUpdate == 6){
            redirectAttributes.addFlashAttribute("messageType1", "success");
            redirectAttributes.addFlashAttribute("message1", "User updated successfully!");
        } else {
            if(isUserUpdate == 0){
                redirectAttributes.addFlashAttribute("messageType1", "danger");
                redirectAttributes.addFlashAttribute("message1", "Username is already exist!");
            } else {
                if(isUserUpdate == 1){
                    redirectAttributes.addFlashAttribute("messageType1", "danger");
                    redirectAttributes.addFlashAttribute("message1", "student ID number is already exist!");
                } else {
                    if(isUserUpdate == 2){
                        redirectAttributes.addFlashAttribute("messageType1", "danger");
                        redirectAttributes.addFlashAttribute("message1", "You have to fill student ID number!");
                    } else {
                        if(isUserUpdate == 3){
                            redirectAttributes.addFlashAttribute("messageType1", "danger");
                            redirectAttributes.addFlashAttribute("message1", "You have to fill student group!");
                        } else {
                            if(isUserUpdate == 4){
                                redirectAttributes.addFlashAttribute("messageType1", "danger");
                                redirectAttributes.addFlashAttribute("message1", "You have to fill teacher disciplines!");
                            } else {
                                redirectAttributes.addFlashAttribute("messageType1", "danger");
                                redirectAttributes.addFlashAttribute("message1", "You have to fill teacher groups!");
                            }
                        }
                    }
                }
            }
        }

        return "redirect:/user/" + user.getId();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}/getGroupsByDisciplines")
    @ResponseBody
    public List<Group> getGroupsByDisciplines(@RequestParam List<Long> disciplineIds){
        return userService.getGroupsByDisciplines(disciplineIds);
    }
}
