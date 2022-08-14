package ru.practicum.shareit.user.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UserDtoRowMapper implements RowMapper<UserDto> {

    @Override
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDto userDto = UserDto.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .email(rs.getString("EMAIL"))
                .build();
        return userDto;
    }

    public static UserDto mapRow(Map<String, Object> map) {
        UserDto userDto = UserDto.builder()
                .id((Long) map.get("ID"))
                .name((String) map.get("NAME"))
                .email((String) map.get("EMAIL"))
                .build();
        return userDto;
    }
}
