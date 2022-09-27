package ru.practicum.shareit.bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.converterDto.Converter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImplRepos;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    BookingServiceImplRepos bookingService;

    ItemDto itemDto;

    UserDto userDto;

    BookingDto booking;

    @BeforeEach
    void createItem() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .ownerId(1L)
                .description("Just an item")
                .isAvailable(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    @BeforeEach
    void createUser() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("garrys2machinima@gmail.com")
                .build();
    }

    @BeforeEach
    void createBooking() {
        booking = BookingDto.builder()
                .bookerId(1L)
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(10))
                .status(Status.WAITING)
                .build();
    }

    @Test
    void shouldCreateBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        userDto.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        bookingService.createBooking(booking, 2L);

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionItemIsUnavailable() {
        itemDto.setIsAvailable(Boolean.FALSE);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        userDto.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 2L),
                String.format("Item with ID: %s is unavailable for booking", booking.getItemId()
                ));

        assertEquals(thrown.getMessage(), String.format("Item with ID: %s is unavailable for booking", booking.getItemId()));
    }

    @Test
    void shouldThrowValidationExceptionInvalidPeriodOfTime() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        userDto.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        booking.setStart(LocalDateTime.now().minusHours(2));
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 2L),
                "Invalid period of time"
        );

        assertEquals("Invalid period of time", thrown.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionItemOwnerCreatesBookingToOwnItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(booking, 1L),
                "You are owner of this item!"
        );

        assertEquals("You are owner of this item!", thrown.getMessage());
    }

    @Test
    void shouldGetBooking() {
        booking.setItem(ItemDtoRowMapper.convertDtoToItem(itemDto));
        booking.setBooker(UserDtoRowMapper.convertDtoToUser(userDto));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(Converter.convertDtoToBooking(booking)));
        assertEquals(booking, bookingService.getBooking(1L, 1L));
    }

    @Test
    void shouldChangeStatusOfBookingToApproved() {
        UserDto booker = UserDto.builder()
                .id(2L)
                .name("JohnBooker")
                .email("garrys2machinima1@gmail.com")
                .build();
        booking.setItem(ItemDtoRowMapper.convertDtoToItem(itemDto));
        booking.setBooker(UserDtoRowMapper.convertDtoToUser(booker));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(Converter.convertDtoToBooking(booking)));

        when(bookingRepository.findOwnerOfItem(anyLong()))
                .thenReturn(1L);
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        BookingDto bookingDto = bookingService.changeStatusBooking(1L, 1L, Boolean.TRUE);
        assertEquals("approved", bookingDto.getStatus().getName());
    }

    @Test
    void shouldChangeStatusOfBookingToRejected() {
        UserDto booker = UserDto.builder()
                .id(2L)
                .name("JohnBooker")
                .email("garrys2machinima1@gmail.com")
                .build();
        booking.setItem(ItemDtoRowMapper.convertDtoToItem(itemDto));
        booking.setBooker(UserDtoRowMapper.convertDtoToUser(booker));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(Converter.convertDtoToBooking(booking)));

        when(bookingRepository.findOwnerOfItem(anyLong()))
                .thenReturn(1L);
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.save(any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        BookingDto bookingDto = bookingService.changeStatusBooking(1L, 1L, Boolean.FALSE);
        assertEquals("rejected", bookingDto.getStatus().getName());
    }

    @Test
    void shouldThrowNotOwnerExceptionChangeStatus() {
        booking.setItem(ItemDtoRowMapper.convertDtoToItem(itemDto));
        booking.setBooker(UserDtoRowMapper.convertDtoToUser(userDto));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(Converter.convertDtoToBooking(booking)));

        when(bookingRepository.findOwnerOfItem(anyLong()))
                .thenReturn(1L);

        NotOwnerException thrown = assertThrows(
                NotOwnerException.class,
                () -> bookingService.changeStatusBooking(2L, 1L, Boolean.TRUE),
                "You are not an owner of this item!"
        );
        assertEquals("You are not an owner of this item!", thrown.getMessage());
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "all", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "all", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdAndDateBetweenStartAndEnd(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "current", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdAndDateBetweenStartAndEnd(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "current", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdAndDateAfterEnd(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "past", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByBookerIdAndDateAfterEnd(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "past", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndDateBeforeStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "future", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndDateBeforeStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "future", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndStatusEqualsOrderByStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "waiting", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndStatusEqualsOrderByStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "waiting", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualNullAndStatusEqualsRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndStatusEqualsOrderByStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "rejected", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfUserWithFromAndSizeEqualOneAndTwoAndStatusEqualsRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByBookerIdAndStatusEqualsOrderByStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfUser(1L, "rejected", 1L, 1L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "all", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "all", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateBetweenStartAndEnd(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "current", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateBetweenStartAndEnd(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "current", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateAfterStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "past", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateAfterStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "past", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateBeforeStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "future", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndDateBeforeStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "future", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndStatusEqualsOrderByStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "waiting", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndStatusEqualsOrderByStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "waiting", 1L, 2L));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualNullAndStatusRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndStatusEqualsOrderByStart(anyLong(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "rejected", null, null));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwnerWithFromAndSizeEqualOneAndTwoAndStatusRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(bookingRepository
                .findAllByOwnerIdAndStatusEqualsOrderByStart(anyLong(), any(), any()))
                .thenReturn(List.of(Converter.convertDtoToBooking(booking)));

        assertEquals(List.of(booking), bookingService.getAllBookingsOfItemsOfOwner(1L, "rejected", 1L, 2L));
    }

    /*@Test
    void shouldThrowValidationExceptionBecauseNonExistentStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsOfUser(1L, "status_all", null, null),
                "Error: Unknown state: status_all"
                );

        assertEquals("Error: Unknown state: status_all", thrown.getMessage());
    }*/
}
