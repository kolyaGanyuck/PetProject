package customloginapplication.controllers;

import customloginapplication.dto.AuthRequest;
import customloginapplication.dto.UserDto;
import customloginapplication.models.User;
import customloginapplication.services.CookieService;
import customloginapplication.services.JwtService;
import customloginapplication.services.UserDetailService;
import customloginapplication.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class LoginController {
    private UserService userService;

    private AuthenticationManager authenticationManager;
    private CookieService cookieService;



    private final UserDetailService userDetailsService;
    private JwtService jwtService;

    public LoginController(UserService userService, AuthenticationManager authenticationManager, CookieService cookieService, UserDetailService userDetailsService, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.cookieService = cookieService;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    //3
    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }

    //4
    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model) {
        User user = userService.findByUsername(userDto.getUsername());
        if (user != null) {
            model.addAttribute("userexist", user);
            return "register";
        }
        userService.save(userDto);
        return "redirect:/register?success";
    }

    //2
    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }

    @PostMapping("/authenticate")
    public String auth(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpServletResponse response) {
        AuthRequest authRequest = new AuthRequest(password, username);
        List<String> userRoles = userDetailsService.getUserRolesByUsername(username);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername(), userRoles);
                response.addCookie(cookieService.createCookie("jwtToken", token));
                response.addCookie(cookieService.createCookie("authenticated", "true"));
                model.addAttribute("key", true);
                log.info("User {} is authenticated", username);
                return "redirect:/";
            } else {
                return "register";
            }
        } catch (AuthenticationException e) {
            log.info("Invalid Username or password for user {}", username);
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}
