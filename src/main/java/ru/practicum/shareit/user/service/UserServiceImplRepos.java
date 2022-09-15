package ru.practicum.shareit.user.service;

import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InvalidDataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import java.util.List;
import java.util.Optional;

@Repository("UserServiceRepos")
public class UserServiceImplRepos implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplRepos(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto user) {
        try {
            User userFromRepos = userRepository.save(UserDtoRowMapper.convertDtoToUser(user));
            return UserDtoRowMapper.convertUserToDto(userFromRepos);
        } catch (JDBCException e) {
            throw new InvalidDataConflictException(String.format("User with email: %s already exists!", user.getEmail()));
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserDtoRowMapper.convertUserToDto(user.get());
        } else {
            throw new NotFoundException(String.format("User with ID: %s has not been found!", userId));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        User user;
        if (userRepository.findById(userId).isPresent()) {
            user = userRepository.findById(userId).get();
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }

            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
        } else {
            throw new NotFoundException(String.format("User with ID: %s has not been found!", userId));
        }

        try {
            return UserDtoRowMapper.convertUserToDto(userRepository.save(user));
        } catch (JDBCException e) {
            throw new InvalidDataConflictException(String.format("User with email: %s already exists!", user.getEmail()));
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserDtoRowMapper.convertListOfUsersToListOfDtoUsers(userRepository.findAll());
    }
}
