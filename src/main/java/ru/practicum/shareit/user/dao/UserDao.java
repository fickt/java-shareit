package ru.practicum.shareit.user.dao;

import org.hibernate.JDBCException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserDao {

    UserDto createUser(User userDto) throws JDBCException;

    UserDto getUser(Long userId);

    void deleteUser(Long userId);

    UserDto editUser(Long userId, UserDto userDto);

    List<UserDto> getAllUsers();
}
