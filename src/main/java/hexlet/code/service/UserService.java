package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    List<User> getAllUsers();

    User createNewUser(UserDto userDto);

    User updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    String getCurrentUsername();

    User getCurrentUser();
}
