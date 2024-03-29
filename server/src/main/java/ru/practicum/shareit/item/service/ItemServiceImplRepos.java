package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.converterDto.Converter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.converter.CommentDtoConverter;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("ItemServiceRepos")
public class ItemServiceImplRepos implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImplRepos(ItemRepository itemRepository, UserRepository userRepository,
                                CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", userId)));

            itemDto.setOwnerId(userId);
            Item item = itemRepository.save(ItemDtoRowMapper.convertDtoToItem(itemDto));
        if (itemDto.getRequestId() != null) {
            itemRepository.saveToRequestAndItemIdTable(item.getRequestId(), item.getId());
        }
        return ItemDtoRowMapper.convertItemToDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        log.info(String.format("Trying to find item with ID: %s ...", itemId));
        Item item = itemRepository
                .findFirstById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID: %s has not been found!", itemId)));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", userId)));
        log.info(MessageFormat.format("getItem found: {0}", item.getName()));

        ItemDto itemDto = ItemDtoRowMapper.convertItemToDto(item);

        if (commentRepository.existsByItemIdAndAuthorId(itemId, userId)) {
            return attachCommentsToItem(itemDto);
        }

        if (!userId.equals(itemDto.getOwnerId())) {
            itemDto.setComments(Collections.emptyList());
            return itemDto;
        }

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStartBefore(itemDto.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        if (lastBooking != null) {
            log.info(String.format("Last booking for item ID: %s has been found!", itemId));
            attachCommentsToItem(ItemDtoRowMapper.convertItemToDto(lastBooking.getItem()));
            itemDto.setLastBooking(Converter.convertBookingToDto(lastBooking));
        }
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfter(itemDto.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        if (nextBooking != null) {
            log.info(String.format("Next booking for item ID: %s has been found!", itemId));
            attachCommentsToItem(ItemDtoRowMapper.convertItemToDto(nextBooking.getItem()));
            itemDto.setNextBooking(Converter.convertBookingToDto(nextBooking));
        }
        log.info(String.format("Sending successfully out itemDTO ID: %s", itemId));
        return attachCommentsToItem(itemDto);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID: %s has not been found!", itemId)));

        if (!item.getOwnerId().equals(userId)) {
            throw new NotOwnerException("You are not an owner of this item!");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }
        ItemDto persistedItemDto = ItemDtoRowMapper.convertItemToDto(itemRepository.save(item));
        return attachCommentsToItem(persistedItemDto);
    }

    @Override
    public List<ItemDto> getListOfItems(Long userId, Long from, Long size) {
        List<ItemDto> itemDtoList;
        if (from == null || size == null) {
            itemDtoList = ItemDtoRowMapper
                    .convertListOfItemsToListOfDtoItems(itemRepository
                            .findItemsByOwnerId(userId));
        } else {
            itemDtoList = ItemDtoRowMapper
                    .convertListOfItemsToListOfDtoItems(itemRepository
                            .findItemsByOwnerId(PageRequest.of(from.intValue(), size.intValue()), userId));
        }
        for (ItemDto itemDto : itemDtoList) {
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBefore(itemDto.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            if (lastBooking != null) {
                log.info(String.format("Last booking for item ID: %s has been found!", itemDto.getId()));
                itemDto.setLastBooking(Converter.convertBookingToDto(lastBooking));
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfter(itemDto.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            if (nextBooking != null) {
                log.info(String.format("Next booking for item ID: %s has been found!", itemDto.getId()));
                itemDto.setNextBooking(Converter.convertBookingToDto(nextBooking));
            }
        }

        return attachCommentsToListOfItems(itemDtoList);
    }

    @Override
    public List<ItemDto> getListOfItemsBySearch(String text, Long from, Long size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<ItemDto> itemDtoList;

        if (from == null || size == null) {
            itemDtoList = ItemDtoRowMapper
                    .convertListOfItemsToListOfDtoItems(itemRepository
                            .findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(text, text));
        } else {
            itemDtoList = ItemDtoRowMapper
                    .convertListOfItemsToListOfDtoItems(itemRepository
                            .findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(PageRequest.of(from.intValue(), size.intValue()), text, text));
        }

        return attachCommentsToListOfItems(itemDtoList);
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository.findFirstByItemId(itemId)
                .orElseThrow(() -> new ValidationException(String.format("Item with ID: %s has no bookings!", itemId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", userId)));
        if (booking.getStatus() != Status.APPROVED) {
            throw new ValidationException(String.format("Booking with ID: %s has not been accepted yet!", booking.getId()));
        }
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        commentDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentDto.setAuthorName(user.getName());
        Comment comment = CommentDtoConverter.convertDtoToComment(commentDto);
        CommentDto persistedComment = CommentDtoConverter.convertCommentToDto(commentRepository.save(comment));
        commentRepository.saveItemIdAndCommentIdToItemCommentTable(persistedComment.getItemId(), persistedComment.getId());
        return persistedComment;
    }

    /**
     * Before sending ItemDto/List<ItemDto> as a response
     * it is needed to attach comments via these methods
     */
    private List<ItemDto> attachCommentsToListOfItems(List<ItemDto> itemDtoList) {
        for (ItemDto itemDto : itemDtoList) {
            List<CommentDto> commentDtoList = commentRepository
                    .findAllByItemId(itemDto.getId()).stream()
                    .map(CommentDtoConverter::convertCommentToDto)
                    .collect(Collectors.toList());
            if (commentDtoList.isEmpty()) {
                itemDto.setComments(Collections.emptyList());
            } else {
                itemDto.setComments(commentDtoList);
            }
        }
        return itemDtoList;
    }

    private ItemDto attachCommentsToItem(ItemDto itemDto) {
        List<CommentDto> commentDtoList = commentRepository
                .findAllByItemId(itemDto.getId()).stream()
                .map(CommentDtoConverter::convertCommentToDto)
                .collect(Collectors.toList());
        if (commentDtoList.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(commentDtoList);
        }
        return itemDto;
    }
}
