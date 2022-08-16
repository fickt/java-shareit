package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import javax.validation.Valid;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(@Valid UserDto userDto) {
            return userDao.createUser(UserDtoRowMapper.convertDtoToUser(userDto));
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
