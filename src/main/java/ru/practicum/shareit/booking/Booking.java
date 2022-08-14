package ru.practicum.shareit.booking;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * // TODO .
 */
public class Booking {
    private long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd")
    private String start;
    @DateTimeFormat(pattern = "uuuu-MM-dd")
    private String end;
    private long itemId;
    private long userId;
    private String status;
}
