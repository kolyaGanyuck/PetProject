package customloginapplication.controllers;

import customloginapplication.services.UserDetailService;
import customloginapplication.services.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserServiceImpl userService;
    private final UserDetailService userDetailService;

    @Autowired
    public AdminController(UserServiceImpl userService, UserDetailService userDetailService) {
        this.userService = userService;
        this.userDetailService = userDetailService;
    }

    @GetMapping("/admin")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.listOfUsers());
        return "userList";
    }

    @PostMapping("/admin/ban/{id}")
    public String banUser(@PathVariable("id") Long id) {
        userDetailService.banUser(id);
        return "redirect:/admin";
    }
    }
