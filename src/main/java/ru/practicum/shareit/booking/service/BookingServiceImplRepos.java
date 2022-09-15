package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new NotOwnerException("You are not an owner of this item !");
        }
        BookingDto bookingUpdatedDto = Converter.convertBookingToDto(bookingRepository.save(booking));
        log.info(MessageFormat.format("Booking has been saved to Database: {0}", bookingUpdatedDto));
        return bookingUpdatedDto;
    }

    @Override
    public List<BookingDto> getAllBookingsOfUser(Long userId, String state) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException((String.format("User with ID: %s has not been found!", userId))));

        LocalDateTime date = LocalDateTime.now();
        if (state == null || state.equalsIgnoreCase("all")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
        } else if (state.equalsIgnoreCase("current")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateBetweenStartAndEnd(userId, date));
        } else if (state.equalsIgnoreCase("past")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateAfterEnd(userId, date));
        } else if (state.equalsIgnoreCase("future")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndDateBeforeStart(userId, date));
        } else if (state.equalsIgnoreCase("waiting")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.WAITING));
        } else if (state.equalsIgnoreCase("rejected")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByBookerIdAndStatusEqualsOrderByStart(userId, Status.REJECTED));
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfItemsOfOwner(Long ownerId, String state) {
        userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundException((String.format("User with ID: %s has not been found!", ownerId))));

        LocalDateTime date = LocalDateTime.now();
        if (state == null || state.equalsIgnoreCase("all")) {
            return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId));
        } else if (state.equalsIgnoreCase("current")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, date));
        } else if (state.equalsIgnoreCase("past")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateAfterStart(ownerId, date));
        } else if (state.equalsIgnoreCase("future")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBeforeStart(ownerId, date));
        } else if (state.equalsIgnoreCase("waiting")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING.getName()));
        } else if (state.equalsIgnoreCase("rejected")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED.getName()));
        } else {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }
}
