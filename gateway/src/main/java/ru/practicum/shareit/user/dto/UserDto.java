package ru.practicum.shareit.user.dto;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    @NotBlank(message = "name should not be empty")
    private String name;
    @Email(message = "invalid email")
    private String email;
}
