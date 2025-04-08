package ru.journal.homework.aggregator.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.service.UserService;

import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("profile")
    String profile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("fio", user.getFio());
        model.addAttribute("birthdate", user.getBirthdate());
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
        model.addAttribute("status", user.getStatus());
        model.addAttribute("group", userService.getGroupNameByUser(user));
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
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("fio", user.getFio());
        model.addAttribute("birthdate", user.getBirthdate());
        return "profile";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUserListDto());
        return "userList";
    }

    /*@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.findAllRole());
        return "userEdit";
    }*/
}
