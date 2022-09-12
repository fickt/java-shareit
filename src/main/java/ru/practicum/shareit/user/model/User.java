package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Entity
@Table(name = "USER_TABLE")
public class User {

    public User() {

    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "name should not be empty")
    private String name;
    @NotBlank(message = "email should not be empty")
    @Email(message = "invalid email")
    @Column(name = "EMAIL", unique = true)
    private String email;
}
