package ru.practicum.shareit.booking.converterDto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static Booking convertDtoToBooking(BookingDto bookingDto) {
       return Booking.builder()
               .start(bookingDto.getStart())
               .end(bookingDto.getEnd())
               .itemId(bookingDto.getItemId())
               .status(bookingDto.getStatus())
               .userId(bookingDto.getUserId())
               .build();
    }

    public static BookingDto convertBookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .status(booking.getStatus())
                .userId(booking.getUserId())
               // .booker(booking.getBooker())
                //.item(booking.getItem())
                .build();
    }

    public static List<BookingDto> convertListOfBookingToDto(List<Booking> bookingList) {
        List<BookingDto> dtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            dtoList.add(convertBookingToDto(booking));
        }
        return dtoList;
    }
}
