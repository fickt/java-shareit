package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;

import java.util.List;


@Service("ItemServiceDao")
@RequiredArgsConstructor
public class ItemServiceImplDao implements ItemService {

    private final ItemDao itemDao;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return itemDao.addItem(userId, ItemDtoRowMapper.convertDtoToItem(itemDto));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemDao.editItem(userId, itemId, itemDto);
    }

    @Override
    public List<ItemDto> getListOfItems(Long userId) {
        return itemDao.getListOfItems(userId);
    }

    @Override
    public List<ItemDto> getListOfItemsBySearch(String text) {
        return itemDao.getListOfItemsBySearch(text);
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        return null;
    }

}
