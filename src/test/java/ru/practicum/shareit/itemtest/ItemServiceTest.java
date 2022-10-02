package ru.practicum.shareit.itemtest;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.converterDto.Converter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.comment.converter.CommentDtoConverter;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;
import ru.practicum.shareit.item.service.ItemServiceImplRepos;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    BookingRepository bookingRepository;

    @Autowired
    ItemServiceImplRepos itemService;

    ItemDto itemDto;

    UserDto userDto;

    BookingDto booking;

    CommentDto comment;

    @BeforeEach
    void createItem() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .ownerId(1L)
                .description("Just an item")
                .isAvailable(Boolean.TRUE)
                .requestId(1L)
                .comments(Collections.emptyList())
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
                .status(Status.WAITING)
                .item(ItemDtoRowMapper.convertDtoToItem(itemDto))
                .build();
    }

    @BeforeEach
    void createComment() {
        comment = CommentDto.builder()
                .text("text")
                .authorName("John")
                .itemId(1L)
                .authorId(1L)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    @Test
    void shouldCreateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(itemRepository.save(any()))
                .thenReturn(ItemDtoRowMapper.convertDtoToItem(itemDto));

        itemService.addItem(1L, itemDto);

        verify(itemRepository, times(1)).saveToRequestAndItemIdTable(anyLong(), anyLong());
    }

    @Test
    @Ignore
    void shouldGetItem() {
        when(itemRepository.findFirstById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        when(bookingRepository.findFirstByItemIdAndStartBefore(anyLong(),any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        when(bookingRepository.findFirstByItemIdAndStartAfter(anyLong(), any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(commentRepository.existsByItemIdAndAuthorId(anyLong(), anyLong()))
                .thenReturn(Boolean.FALSE);

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());

        itemService.getItem(1L, 1L);

        verify(bookingRepository, times(1)).findFirstByItemIdAndStartBefore(anyLong(), any());
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartAfter(anyLong(), any());
    }

    @Test
    void shouldEditItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        when(itemRepository.save(any()))
                .thenReturn(ItemDtoRowMapper.convertDtoToItem(itemDto));

        assertEquals(itemDto, itemService.editItem(1L, 1L, itemDto));
    }

    @Test
    void shouldReturnListOfItems() {
        when(itemRepository.findItemsByOwnerId(any(), anyLong()))
                .thenReturn(List.of(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ItemDtoRowMapper.convertDtoToItem(itemDto)));

        when(bookingRepository.findFirstByItemIdAndStartBefore(anyLong(),any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        when(bookingRepository.findFirstByItemIdAndStartAfter(anyLong(), any()))
                .thenReturn(Converter.convertDtoToBooking(booking));

        itemDto.setNextBooking(booking);
        itemDto.setLastBooking(booking);

        assertEquals(itemService.getListOfItems(1L, null, null), itemService.getListOfItems(1L, null, null));

        assertEquals(List.of(itemDto), itemService.getListOfItems(1L, 1L, 2L));

        verify(bookingRepository, times(3)).findFirstByItemIdAndStartBefore(anyLong(), any());
        verify(bookingRepository, times(3)).findFirstByItemIdAndStartAfter(anyLong(), any());

    }

    @Test
    void shouldReturnEmptyListOfItemsBySearch() {
        assertEquals(Collections.emptyList(), itemService.getListOfItemsBySearch(" ", null, null));
    }

    @Test
    void shouldReturnListOfItemsBySearchWithTextButFromAndSizeEqualNull() {
        when(itemRepository.findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(anyString(), anyString()))
                .thenReturn(List.of(ItemDtoRowMapper.convertDtoToItem(itemDto)));
        assertEquals(List.of(itemDto), itemService.getListOfItemsBySearch("item", null, null));
          verify(itemRepository, times(1))
                .findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(anyString(), anyString());
    }

    @Test
    void shouldReturnListOfItemsBySearchWithTextAndFromAndSizeEqualOne() {
        when(itemRepository.findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(any(), anyString(), anyString()))
                .thenReturn(List.of(ItemDtoRowMapper.convertDtoToItem(itemDto)));
        assertEquals(List.of(itemDto), itemService.getListOfItemsBySearch("item", 1L, 1L));
         verify(itemRepository, times(1))
                .findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(any(), anyString(), anyString());
    }

    @Test
    void shouldCreateCommentToItem() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findFirstByItemId(anyLong()))
                .thenReturn(Optional.ofNullable(Converter.convertDtoToBooking(booking)));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(commentRepository.save(any()))
                .thenReturn(CommentDtoConverter.convertDtoToComment(comment));

        assertEquals(itemService.addCommentToItem(1L, 1L, comment), comment);

        verify(bookingRepository, times(1)).findFirstByItemId(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }
}
