package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
/**
 * Class used to edit already existing users.
 * name can be null
 * email can be null as well, if not null, must be correctly formed as email
*/

@Data
public class UserDtoPatch {
        private String name;
        @Email(message = "invalid email")
        private String email;
}
