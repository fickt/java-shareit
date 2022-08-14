package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemDtoValidator {
    public static boolean isValid(ItemDto itemDto) {
        if (itemDto.getIsAvailable() == null
                || itemDto.getName() == null
                || itemDto.getName().isBlank()
                || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
