package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.status.Status;


@Data
@Builder
public class Booking {
    private Long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd")
    private String start;
    @DateTimeFormat(pattern = "uuuu-MM-dd")
    private String end;
    private Long itemId;
    private Long userId;
    private Status status;
}
