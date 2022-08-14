package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

import static ru.practicum.shareit.item.validator.ItemDtoValidator.isValid;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        if (isValid(itemDto)) {
            return itemDao.addItem(userId, itemDto);
        } else {
            throw new ValidationException("invalid request body ItemDto");
        }
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public ItemDto editItem(long userId, long itemId, ItemDto itemDto) {
        return itemDao.editItem(userId, itemId, itemDto);
    }

    @Override
    public List<ItemDto> getListOfItems(long userId) {
        return itemDao.getListOfItems(userId);
    }

    @Override
    public List<ItemDto> getListOfItemsBySearch(String text) {
        return itemDao.getListOfItemsBySearch(text);
    }
}
