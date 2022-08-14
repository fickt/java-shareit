package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDao {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> getListOfItems(Long userId);

    List<ItemDto> getListOfItemsBySearch(String text);
}
