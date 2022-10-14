package ru.practicum.shareit.bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.converterDto.Converter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingJsonTest {

    Booking booking;

    BookingDto bookingDto;

    @BeforeEach
    void createBooking() {
        booking = Booking.builder()
                .id(1L)
                .bookerId(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2001, 12, 12, 12, 12))
                .end(LocalDateTime.of(2002, 1, 1, 1, 1))
                .build();
    }

    @BeforeEach
    void createBookingDto() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .bookerId(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2001, 12, 12, 12, 12))
                .end(LocalDateTime.of(2002, 1, 1, 1, 1))
                .build();
    }

    @Test
    void shouldConvertBookingToDto() {
        bookingDto = null;
        BookingDto bookingDto = Converter.convertBookingToDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBookerId(), bookingDto.getBookerId());
        assertEquals(booking.getItemId(), bookingDto.getItemId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void shouldConvertDtoToBooking() {
        booking = null;
        booking = Converter.convertDtoToBooking(bookingDto);
        assertNull(booking.getId());                                 //only database can assign value to ID
        assertEquals(bookingDto.getBookerId(), booking.getBookerId());
        assertEquals(bookingDto.getItemId(), booking.getItemId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
    }

    @Test
    void shouldConvertListOfBookingsToListOfDto() {
        List<BookingDto> bookingDtoList = Converter.convertListOfBookingToDto(List.of(booking));
        assertFalse(bookingDtoList.isEmpty());
        BookingDto convertedBooking = bookingDtoList.get(0);
        assertEquals(booking.getBookerId(), convertedBooking.getBookerId());
        assertEquals(booking.getItemId(), convertedBooking.getItemId());
        assertEquals(booking.getStart(), convertedBooking.getStart());
        assertEquals(booking.getEnd(), convertedBooking.getEnd());
    }
}
