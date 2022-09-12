package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime end;
    transient private Long itemId;
    transient private Long bookerId;
    private Item item;
    private User booker;
    private Status status;
}
