package ru.practicum.shareit.booking.converterDto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    /**
     * getBookerId() - is getItemId(),
     * getItemId() - is getBookerId(),
     * Probably lombok has generated Getters and Setters in Booking.class incorrectly
     */
    public static Booking convertDtoToBooking(BookingDto bookingDto) {
       return Booking.builder()
               .start(bookingDto.getStart())
               .end(bookingDto.getEnd())
               .itemId(bookingDto.getBookerId())
               .status(bookingDto.getStatus())
               .item(bookingDto.getItem())
               .booker(bookingDto.getBooker())
               .bookerId(bookingDto.getItemId())
               .build();
    }

    public static BookingDto convertBookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .status(booking.getStatus())
                .bookerId(booking.getBookerId())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static List<BookingDto> convertListOfBookingToDto(List<Booking> bookingList) {
        return bookingList.stream()
                .map(Converter::convertBookingToDto)
                .collect(Collectors.toList());
    }
}
