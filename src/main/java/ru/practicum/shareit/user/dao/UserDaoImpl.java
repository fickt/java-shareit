package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InvalidDataConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    private static final String SQL_CREATE_USER = "INSERT INTO USER_TABLE (NAME, EMAIL) VALUES (?,?)";

    private static final String SQL_GET_USER = "SELECT * FROM USER_TABLE WHERE ID=?";

    private static final String SQL_DELETE_USER = "DELETE FROM USER_TABLE WHERE ID=?";

    private static final String SQL_UPDATE_NAME_OF_USER = "UPDATE USER_TABLE SET NAME=? WHERE ID=?";

    private static final String SQL_UPDATE_EMAIL_OF_USER = "UPDATE USER_TABLE SET EMAIL=? WHERE ID=?";

    private static final String SQL_GET_ALL_USERS = "SELECT * FROM USER_TABLE";

    private static final String SQL_CHECK_EMAIL_IF_EXISTS = "SELECT COUNT(EMAIL) FROM USER_TABLE WHERE EMAIL=?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(User user) {

        Long isEmailPresent = jdbcTemplate.queryForObject(SQL_CHECK_EMAIL_IF_EXISTS, Long.class, user.getEmail());

        if (isEmailPresent == 0) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_USER, new String[]{"ID"});
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                return stmt;
            }, keyHolder);
            return getUser(keyHolder.getKey().longValue());
        } else {
            log.warn("User with email: " + user.getEmail() + " already exists!");
            throw new InvalidDataConflictException("User with email: " + user.getEmail() + " already exists!");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        return jdbcTemplate.queryForObject(SQL_GET_USER, new UserDtoRowMapper(), userId);
    }

    @Override
    public void deleteUser(Long userId) {
        jdbcTemplate.update(SQL_DELETE_USER, userId);
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        try {
            if (userDto.getName() != null) {
                jdbcTemplate.update(SQL_UPDATE_NAME_OF_USER, userDto.getName(), userId);
            }

            if (userDto.getEmail() != null) {
                jdbcTemplate.update(SQL_UPDATE_EMAIL_OF_USER, userDto.getEmail(), userId);
            }
            return getUser(userId);
        } catch (Exception e) {
            log.warn("User with email: " + userDto.getEmail() + " already exists!");
            throw new InvalidDataConflictException("User with email: " + userDto.getEmail() + " already exists!");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<Map<String, Object>> mapOfUsers = jdbcTemplate.queryForList(SQL_GET_ALL_USERS);
        List<UserDto> listOfUsers = new ArrayList<>();
        for (Map<String, Object> map : mapOfUsers) {
            listOfUsers.add(UserDtoRowMapper.mapRow(map));
        }
        return listOfUsers;
    }
}
