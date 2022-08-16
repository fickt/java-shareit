package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userServiceImpl;

    @Autowired
    public UserController(UserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userServiceImpl.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userServiceImpl.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@PathVariable Long userId,
                            @RequestBody UserDto userDto) {
       return userServiceImpl.editUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userServiceImpl.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }
}
