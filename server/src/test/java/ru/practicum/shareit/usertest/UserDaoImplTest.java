package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserDaoImplTest {

    @Autowired
    UserDao userDao;

    User user;

    @BeforeEach
    void createUser() {
        user = User.builder()
                .id(1L)
                .email("garrys2machinima@gmail.com")
                .name("Name")
                .build();
    }

    @Test
    void shouldCreateUser() {
        UserDto userResult = userDao.createUser(user);
        assertEquals(user.getId(), userResult.getId());
        assertEquals(user.getName(), userResult.getName());
        assertEquals(user.getEmail(), userResult.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        assertFalse(userDao.getAllUsers().isEmpty());
        userDao.deleteUser(1L);
        assertTrue(userDao.getAllUsers().isEmpty());
    }

    @Test
    void shouldEditUser() {
        userDao.createUser(user);
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("editedName")
                .email("editedEmail@mail.ru")
                .build();
        userDao.editUser(2L, userDto);
        UserDto userDtoResult = userDao.getUser(2L);
        assertEquals("editedName", userDtoResult.getName());
        assertEquals("editedEmail@mail.ru", userDtoResult.getEmail());
    }
}
