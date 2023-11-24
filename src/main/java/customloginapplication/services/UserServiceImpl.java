package customloginapplication.services;

import customloginapplication.models.Role;
import customloginapplication.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import customloginapplication.dto.UserDto;
import customloginapplication.models.User;
import customloginapplication.repositories.UserRepository;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }




    @Override
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public User save(UserDto userDto) {
        User user = new User();
        user.setFullname(userDto.getFullname());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCity(userDto.getCity());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setActive(true);
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        user.getRoles().add(userRole);
        return userRepository.save(user);
    }


    public List<User> listOfUsers() {
        return userRepository.findAll();
    }



}
