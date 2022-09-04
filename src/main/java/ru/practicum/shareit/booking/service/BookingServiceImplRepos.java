package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImplRepos implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImplRepos (BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        Optional<Item> itemOpt = itemRepository.findById(bookingDto.getItemId());

        if (itemOpt.isEmpty()) {
            throw new NotFoundException(String.format("item with ID: %s has not been found!", bookingDto.getItemId()));
        }

        if (!itemOpt.get().getIsAvailable()) {
            throw new ValidationException(String.format("Item with ID: %s is unavailable for booking", bookingDto.getItemId()));
        }

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException((String.format("User with ID: %s has not been found!", userId)));
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid period of time");
        }

        bookingDto.setUserId(userId);
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingRepository.save(Converter.convertDtoToBooking(bookingDto));
        return Converter.convertBookingToDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        return Converter.convertBookingToDto(bookingRepository.findById(bookingId).get());
    }

    @Override
    public BookingDto changeStatusBooking(Long ownerId, Long bookingId, Boolean isApproved) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException(String.format("Booking with ID: %s has not been found!", bookingId));
        }

        Booking booking = bookingOpt.get();

        if (ownerId.equals(bookingRepository.findOwnerOfItem(booking.getItemId()))) {
            if (isApproved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new NotOwnerException("You are not an owner of this item !");
        }
        return Converter.convertBookingToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsOfUser(Long userId, String state) {
        LocalDate date = LocalDate.now();
        String format = "uuuu-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        if (state == null) {
            return Converter.convertListOfBookingToDto(bookingRepository.findAllByUserId(userId));
        } else if (state.equals("current")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndDateBetweenStartAndEnd(userId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("past")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndDateAfterEnd(userId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("future")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndDateBeforeStart(userId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("waiting")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndStatusEqualsOrderByStart(userId, Status.WAITING));
        } else if (state.equals("rejected")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndStatusEqualsOrderByStart(userId, Status.REJECTED));
        } else {
            throw new NotFoundException("Unknown request");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfItemsOfOwner(Long ownerId, String state) {
        LocalDate date = LocalDate.now();
        String format = "uuuu-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        if (state == null) {
            return Converter.convertListOfBookingToDto(bookingRepository.findAllByOwnerId(ownerId));
        } else if (state.equals("current")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBetweenStartAndEnd(ownerId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("past")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateAfterEnd(ownerId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("future")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndDateBeforeStart(ownerId, LocalDate.parse(date.format(dateTimeFormatter))));
        } else if (state.equals("waiting")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByOwnerIdAndStatusEqualsOrderByStart(ownerId, Status.WAITING));
        } else if (state.equals("rejected")) {
            return Converter
                    .convertListOfBookingToDto(bookingRepository
                            .findAllByUserIdAndStatusEqualsOrderByStart(ownerId, Status.REJECTED));
        } else {
            throw new NotFoundException("Unknown request");
        }
    }
}
