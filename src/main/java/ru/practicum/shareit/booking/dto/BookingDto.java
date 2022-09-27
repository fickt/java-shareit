package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto implements Serializable {
    private Long id;
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime start;
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;
    private transient Long itemId;
    private transient Long bookerId;
    private Item item;
    private User booker;
    private Status status;
}
