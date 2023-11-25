package customloginapplication.services;

import java.util.*;
import java.util.stream.Collectors;

import customloginapplication.models.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import customloginapplication.models.User;
import customloginapplication.repositories.UserRepository;
import org.springframework.ui.Model;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.info("Username {} or Password not found", username);
            throw new UsernameNotFoundException("Username or Password not found");

        }

        if (!user.isActive()) {
            log.info("{} user is banned", user);
            throw new UsernameNotFoundException("This user is banned");
        }

        return new UserDetail(
                user.getUsername(),
                user.getPassword(),
                authorities(),
                user.getFullname());
    }
    public List<String> getUserRolesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(null);
        if (user.isActive()) {
            user.setActive(false);
            log.info("User have {} been banned", user.getUsername());
        } else {
            user.setActive(true);
            log.info("User have {} been unbanned", user.getUsername());
        }
        userRepository.save(user);
    }

    public String getAuthenticatedValueFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authenticated".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "false"; // Значення за замовчуванням, якщо куки "authenticated" не знайдено
    }

    public void updateUserById(User user) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setFullname(user.getFullname());
            existingUser.setCity(user.getCity());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            userRepository.save(existingUser);
            log.info("Update information about user {}", user.getUsername());


        }
    }



    public void handleAuthenticatedUser(Model model, HttpServletRequest request) {
        String auth = getAuthenticatedValueFromCookie(request);
        boolean isAuthenticated = "true".equals(auth);
        model.addAttribute("key", isAuthenticated);
    }



    public Collection<? extends GrantedAuthority> authorities() {
        return Arrays.asList(new SimpleGrantedAuthority("USER"));
    }


}
