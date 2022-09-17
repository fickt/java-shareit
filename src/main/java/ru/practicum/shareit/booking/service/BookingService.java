package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto getBooking(Long userId, Long bookingId);

    BookingDto changeStatusBooking(Long ownerId, Long bookingId, Boolean isApproved);

    List<BookingDto> getAllBookingsOfUser(Long userId, String state, Long from, Long size);

    List<BookingDto> getAllBookingsOfItemsOfOwner(Long ownerId, String state, Long from, Long size);
}
