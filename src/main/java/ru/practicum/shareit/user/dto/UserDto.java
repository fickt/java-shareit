package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotNull(message = "name should not be empty")
    private String name;
    @NotNull(message = "email should not be empty")
    @Email(message = "invalid email")
    private String email;
}
