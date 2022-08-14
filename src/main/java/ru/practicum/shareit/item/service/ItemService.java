package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto getItem(long itemId);

    ItemDto editItem(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> getListOfItems (long userId);

    List<ItemDto> getListOfItemsBySearch(String text);
}
