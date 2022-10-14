package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dao.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImplDao;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplDaoTest {
    @MockBean
    UserDaoImpl userDao;

    @Autowired
    UserServiceImplDao userService;

    UserDto userDto;

    @BeforeEach
    void createUserDto() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("garrys2machinima@gmail.com")
                .build();
    }

    @Test
    void shouldCreateUser() {
        when(userDao.createUser(any()))
                .thenReturn(userDto);
        userService.createUser(userDto);
        verify(userDao, times(1)).createUser(any());
    }

    @Test
    void shouldGetUser() {
        when(userDao.getUser(anyLong()))
                .thenReturn(userDto);
        userService.getUser(1L);
        verify(userDao, times(1)).getUser(anyLong());
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1L);
        verify(userDao, times(1)).deleteUser(anyLong());
    }

    @Test
    void shouldEditUser() {
        when(userDao.editUser(anyLong(), any()))
                .thenReturn(userDto);
        userService.editUser(1L, userDto);
        verify(userDao, times(1)).editUser(anyLong(), any());
    }

    @Test
    void shouldGetAllUsers() {
        when(userDao.getAllUsers())
                .thenReturn(List.of(userDto));
        userService.getAllUsers();
        verify(userDao, times(1)).getAllUsers();
    }
}
