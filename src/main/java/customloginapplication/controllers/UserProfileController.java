package customloginapplication.controllers;

import customloginapplication.models.User;
import customloginapplication.services.CookieService;
import customloginapplication.services.JwtService;
import customloginapplication.services.UserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class UserProfileController {
    private final UserDetailService userDetailsService;
    private JwtService jwtService;
    private CookieService cookieService;

    @Autowired
    public UserProfileController(UserDetailService userDetailsService, JwtService jwtService, CookieService cookieService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
    }

    @GetMapping("/userProfile")
    public String userProfile(Principal principal, Model model, HttpServletRequest request) {
        if (principal != null) {
            userDetailsService.handleAuthenticatedUser(model, request);
            User user = userDetailsService.findByUsername(principal.getName());
            model.addAttribute("user", user);
            return "userProfile";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/updateUserInfo")
    public String updateUserData(@ModelAttribute User user, HttpServletResponse response) {
        List<String> userRoles = userDetailsService.getUserRolesByUsername(user.getUsername());
        userDetailsService.updateUserById(user);
        String token = jwtService.generateToken(user.getUsername(), userRoles);
        response.addCookie(cookieService.createCookie("jwtToken", token));
        return "redirect:/userProfile";
    }
}
