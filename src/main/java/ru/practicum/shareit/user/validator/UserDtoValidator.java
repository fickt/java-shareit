package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.user.dto.UserDto;

public class UserDtoValidator {

    public static boolean isValid(UserDto userDto) {
        if (userDto.getName() == null
                || userDto.getName().isBlank()
                || userDto.getEmail() == null
                || userDto.getEmail().isBlank()
                || !userDto.getEmail().contains("@")) {
            return false;
        } else {
            return true;
        }
    }
}
