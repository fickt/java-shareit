package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserJsonTest {

    User user;

    UserDto userDto;

    @BeforeEach
    void createUser() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("garrys2machinima@gmail.com")
                .build();
    }

    @BeforeEach
    void createUserDto() {
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("garrys2machinima@gmail.com")
                .build();
    }

    @Test
    void shouldConvertUserToDto() {
        userDto = null;
        userDto = UserDtoRowMapper.convertUserToDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void shouldConvertDtoToUser() {
        user = null;
        user = UserDtoRowMapper.convertDtoToUser(userDto);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
    }
}
