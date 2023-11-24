package customloginapplication.services;

import customloginapplication.dto.UserDto;
import customloginapplication.models.User;

import java.util.List;

public interface UserService {

	User findByUsername(String username);
	User save (UserDto userDto);
	List<User> listOfUsers();






}
