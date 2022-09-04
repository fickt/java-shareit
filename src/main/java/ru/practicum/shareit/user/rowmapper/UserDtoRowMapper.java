package ru.practicum.shareit.user.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDtoRowMapper implements RowMapper<UserDto> {

    @Override
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserDto.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .email(rs.getString("EMAIL"))
                .build();
    }

    public static UserDto mapRow(Map<String, Object> map) {
        return UserDto.builder()
                .id((Long) map.get("ID"))
                .name((String) map.get("NAME"))
                .email((String) map.get("EMAIL"))
                .build();
    }

    public static UserDto convertUserToDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User convertDtoToUser(UserDto user) {
        return User
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserDto> convertListOfUsersToListOfDtoUsers(List<User> userList) {
        List<UserDto> userDtoList = new ArrayList<>();
        for(User user : userList) {
            userDtoList.add(convertUserToDto(user));
        }
        return userDtoList;
    }
}
