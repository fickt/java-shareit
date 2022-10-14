package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "1") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> changeStatusBooking(@RequestHeader("X-Sharer-User-Id") long userId,
													  @PathVariable Long bookingId,
													  @RequestParam("approved") Boolean isApproved) {
		log.info("Patch booking {}, userId={}", bookingId, userId);
		return bookingClient.patchBooking(userId, bookingId, isApproved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsOfItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
															   @RequestParam(value = "state", required = false) String stateParam,
															   @PositiveOrZero @RequestParam(value = "from", defaultValue = "1") Long from,
															   @Positive @RequestParam(value = "size", defaultValue = "10") Long size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
		log.info("Get bookings of item owner with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);

		return bookingClient.getBookingsOfItemsOfOwner(ownerId, state.name(), from, size);
	}
}
