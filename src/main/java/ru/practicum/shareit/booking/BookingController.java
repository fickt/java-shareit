package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {

        return bookingService.getBooking(userId, bookingId);
    }

    /**
     * via this method only owner can change status of item (approve/disapprove)
     */
    @PatchMapping("/{bookingId}")
    public BookingDto changeStatusBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @PathVariable Long bookingId,
                                          @RequestParam("approved") Boolean isApproved) {
        return bookingService.changeStatusBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "state", required = false) String state) {
       return bookingService.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @RequestParam(value = "state", required = false) String state) {
        return bookingService.getAllBookingsOfItemsOfOwner(ownerId, state);
    }
}
