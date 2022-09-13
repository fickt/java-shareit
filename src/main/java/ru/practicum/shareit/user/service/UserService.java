package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUser(Long userId);

    void deleteUser(Long userId);

    UserDto editUser(Long userId, UserDto userDto);

    List<UserDto> getAllUsers();
}
