package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(final Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().toList();
    }

    public User createNewUser(final UserDto userDto) {
        final User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(newUser);
    }

    public User updateUser(final Long id, final UserDto userDto) {
        final User userForUpdate = userRepository.findById(id).orElseThrow();
        userForUpdate.setEmail(userDto.getEmail());
        userForUpdate.setFirstName(userDto.getFirstName());
        userForUpdate.setLastName(userDto.getLastName());
        userForUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(userForUpdate);
    }

    public void deleteUser(final Long id) {
        final User userForDelete = userRepository.findById(id).orElseThrow();
        userRepository.delete(userForDelete);
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername()).orElseThrow();
    }
}
