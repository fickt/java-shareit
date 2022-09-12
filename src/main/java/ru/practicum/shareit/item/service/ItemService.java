package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId, Long userId);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> getListOfItems(Long userId);

    List<ItemDto> getListOfItemsBySearch(String text);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
