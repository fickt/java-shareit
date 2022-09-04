package ru.practicum.shareit.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "USER_TABLE")
public class User {

    public User() {

    }

    public User(Long id, String name, String email, List<Booking> bookings ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bookings = bookings;
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
    @JsonIgnore
    @OneToMany(mappedBy = "booker")
    List<Booking> bookings = new ArrayList<>();
}
