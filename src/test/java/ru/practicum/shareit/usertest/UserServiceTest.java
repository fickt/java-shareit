package ru.practicum.shareit.usertest;

import org.hibernate.JDBCException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.InvalidDataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;
import ru.practicum.shareit.user.service.UserServiceImplRepos;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserServiceImplRepos userService;

    @MockBean
    UserRepository userRepository;

    UserDto userDto;

    @BeforeEach
    void createUser() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("garrys2machinima@gmail.com")
                .build();
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(UserDtoRowMapper.convertDtoToUser(userDto)))
                .thenReturn(UserDtoRowMapper.convertDtoToUser(userDto));
        assertEquals(userService.createUser(userDto), userDto);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldGetUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));
        assertEquals(userService.getUser(1L), userDto);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void shouldEditUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(userRepository.save(any()))
                .thenReturn(UserDtoRowMapper.convertDtoToUser(userDto));

        assertEquals(userService.editUser(1L, userDto), userDto);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void shouldReturnListOfAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(UserDtoRowMapper.convertDtoToUser(userDto)));
        assertEquals(userService.getAllUsers(), List.of(userDto));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionEmailAlreadyExists() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        given(userRepository.save(any()))
                .willAnswer(invocation ->
                        {
                            throw new JDBCException(null, null);
                        }
                );

        InvalidDataConflictException thrown = assertThrows(
                InvalidDataConflictException.class,
                () -> userService.editUser(1L, userDto),
                String.format("User with email: %s already exists!", userDto.getEmail()
                ));

        assertEquals(thrown.getMessage(), String.format("User with email: %s already exists!", userDto.getEmail()));
    }

    @Test
    void shouldThrowNotFoundException() {
        given(userRepository.save(any()))
                .willAnswer(invocation ->
                        {
                            throw new NotFoundException(String.format("User with ID: %s has not been found!", 1L));
                        }
                );

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userService.getUser(1L),
                String.format("User with ID: %s has not been found!", 1L
                ));

        assertEquals(thrown.getMessage(),String.format("User with ID: %s has not been found!", 1L));

    }
}
