package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.converterDto.Converter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class BookingServiceImplRepos implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImplRepos(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        Item item = itemRepository
                .findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("item with ID: %s has not been found!", bookingDto.getItemId())));

        if (!item.getIsAvailable()) {
            log.warn(MessageFormat.format("Item with ID: {0} is unavailable for booking", bookingDto.getItemId()));
            throw new ValidationException(String.format("Item with ID: %s is unavailable for booking", bookingDto.getItemId()));
        }

        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException((String.format("User with ID: %s has not been found!", userId))));

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            log.warn(MessageFormat.format("Invalid period of time, booking: {0}", bookingDto));
            throw new ValidationException("Invalid period of time");
        }

        if (item.getOwnerId().equals(userId)) {
            log.warn(MessageFormat.format("You are owner of this item! User ID: {0}", userId));
            throw new NotFoundException("You are owner of this item!");
        }

        bookingDto.setBookerId(userId);
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingRepository.save(Converter.convertDtoToBooking(bookingDto));
        log.info(MessageFormat.format("Booking has been saved to Database: {0}", booking));
        return Converter.convertBookingToDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        BookingDto bookingDto = Converter.convertBookingToDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID: %s has not been found!", bookingId))));
        if (!bookingDto.getBooker().getId().equals(userId) && !bookingDto.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException(MessageFormat.format("You are not a booker of booking with ID: {0} or " +
                    "not an owner of item with ID: {1}", bookingId, bookingDto.getItem().getId()));
        }
        return bookingDto;
    }

    @Override
    public BookingDto changeStatusBooking(Long ownerId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID: %s has not been found!", bookingId)));

        if (booking.getStatus().equals(Status.APPROVED)) {
            log.warn(MessageFormat.format("Booking with ID: {0} is already approved!", bookingId));
            throw new ValidationException(String.format("Booking with ID: %s is already approved!", bookingId));
        }

        if (booking.getBooker().getId().equals(ownerId)) {
            throw new NotFoundException("Booker can not change status of Booking, only owner of item!");
        }

        if (ownerId.equals(bookingRepository.findOwnerOfItem(booking.getItem().getId()))) {
            if (Boolean.TRUE.equals(isApproved)) {
                log.info(MessageFormat.format("Booking {0} with status {1}, is updated to status {2}",
                        booking, booking.getStatus(), Status.APPROVED));
                booking.setStatus(Status.APPROVED);
            } else {
                log.info(MessageFormat.format("Booking {0} with status {1}, is updated to status {2}",
                        booking, booking.getStatus(), Status.REJECTED));
                booking.setStatus(Status.REJECTED);
            }
        } else {
            log.warn(MessageFormat.format("User with ID: {0} is not an owner of item with ID: {1}",
                    ownerId, booking.getItem().getId()));
            throw new NotOwnerException("You are not an owner of this item!");
        }
        BookingDto bookingUpdatedDto = Converter.convertBookingToDto(bookingRepository.save(booking));
        log.info(MessageFormat.format("Booking has been saved to Database: {0}", bookingUpdatedDto));
        return bookingUpdatedDto;
    }

    @Override
    public List<BookingDto> getAllBookingsOfUser(Long userId, String status, Long from, Long size) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException((String.format("User with ID: %s has not been found!", userId))));

        if (status.equals(null)) {
            status = "all";
        }

        if (from == null || size == null) {
            return getBookingsOfUserNoPagination(userId, status);
        }
        return getBookingsOfUserWithPagination(userId, status, from, size);

        /*LocalDateTime date = LocalDateTime.now();
        if (state == null || state.equalsIgnoreCase("all")) {
            if (from == null || size == null) {
                return Converter//
                        .convertListOfBookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId,
                            PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("current")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBetweenStartAndEnd(userId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateBetweenStartAndEnd(userId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("past")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateAfterEnd(userId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateAfterEnd(userId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("future")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBeforeStart(userId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateBeforeStart(userId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("waiting")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.WAITING));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.WAITING,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("rejected")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.REJECTED));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.REJECTED,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }*/
    }

    @Override
    public List<BookingDto> getAllBookingsOfItemsOfOwner(Long ownerId, String status, Long from, Long size) {
        userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundException((String.format("User with ID: %s has not been found!", ownerId))));

        if (status.equals(null)) {
            status = "all";
        }
        if (from == null || size == null) {
            return getAllBookingsOfItemsOfOwnerNoPagination(ownerId, status);
        }
        return getAllBookingsOfItemsOfOwnerWithPagination(ownerId, status, from, size);

        /*LocalDateTime date = LocalDateTime.now();
        if (state == null || state.equalsIgnoreCase("all")) {
            if (from == null || size == null) {
                return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId));
            }
            return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId,
                    PageRequest.of(from.intValue() - 1, size.intValue())));
        } else if (state.equalsIgnoreCase("current")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("past")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateAfterStart(ownerId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateAfterStart(ownerId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("future")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBeforeStart(ownerId, date));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBeforeStart(ownerId, date,
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("waiting")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING.getName()));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING.getName(),
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else if (state.equalsIgnoreCase("rejected")) {
            if (from == null || size == null) {
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED.getName()));
            }
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED.getName(),
                                    PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }*/
    }

    private List<BookingDto> getBookingsOfUserNoPagination(Long userId, String status) {
        LocalDateTime date = LocalDateTime.now();

        switch (status.toLowerCase()) {
            case "all":
                return Converter
                        .convertListOfBookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));

            case "current":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBetweenStartAndEnd(userId, date));

            case "past":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateAfterEnd(userId, date));

            case "future":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBeforeStart(userId, date));

            case "waiting":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.WAITING));

            case "rejected":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.REJECTED));

            default: throw new ValidationException(String.format("Unknown state: %s", status));
        }
    }

    private List<BookingDto> getBookingsOfUserWithPagination(Long userId, String status, Long from, Long size) {
        LocalDateTime date = LocalDateTime.now();

        switch (status.toLowerCase()) {
            case "all":
                return Converter
                        .convertListOfBookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId,
                                PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "current":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBetweenStartAndEnd(userId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "past":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateAfterEnd(userId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "future":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndDateBeforeStart(userId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "waiting":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.WAITING,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "rejected":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.REJECTED,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            default: throw new ValidationException(String.format("Unknown state: %s", status));
        }
    }

    private List<BookingDto> getAllBookingsOfItemsOfOwnerNoPagination(Long ownerId, String status) {
        LocalDateTime date = LocalDateTime.now();

        switch (status.toLowerCase()) {
            case "all":
                return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId));

            case "current":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, date));

            case "past":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateAfterStart(ownerId, date));

            case "future":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBeforeStart(ownerId, date));

            case "waiting":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING.getName()));

            case "rejected":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED.getName()));

            default: throw new ValidationException(String.format("Unknown state: %s", status));
        }
    }

    private List<BookingDto> getAllBookingsOfItemsOfOwnerWithPagination(Long ownerId, String status, Long from, Long size) {
        LocalDateTime date = LocalDateTime.now();

        switch (status.toLowerCase()) {
            case "all":
                return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId,
                        PageRequest.of(from.intValue() - 1, size.intValue())));

            case "current":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "past":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateAfterStart(ownerId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "future":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndDateBeforeStart(ownerId, date,
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "waiting":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING.getName(),
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            case "rejected":
                return Converter
                        .convertListOfBookingToDto(bookingRepository
                                .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED.getName(),
                                        PageRequest.of(from.intValue() - 1, size.intValue(), Sort.by("start").descending())));

            default: throw new ValidationException(String.format("Unknown state: %s", status));
        }
    }
}
