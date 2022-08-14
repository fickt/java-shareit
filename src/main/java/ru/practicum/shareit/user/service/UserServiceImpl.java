package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.user.validator.UserDtoValidator.isValid;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto user) {
        if (isValid(user)) {
            return userDao.createUser(user);
        } else {
            throw new ValidationException("Invalid request body UserDto");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        return userDao.getUser(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteUser(userId);
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        return userDao.editUser(userId, userDto);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }
}
