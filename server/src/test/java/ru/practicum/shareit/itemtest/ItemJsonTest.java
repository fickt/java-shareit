package ru.practicum.shareit.itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class ItemJsonTest {

    Item item;

    ItemDto itemDto;

    @BeforeEach
    void createItem() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .isAvailable(Boolean.TRUE)
                .requestId(1L)
                .ownerId(1L)
                .build();
    }

    @BeforeEach
    void createItemDto() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .isAvailable(Boolean.TRUE)
                .requestId(1L)
                .ownerId(1L)
                .build();
    }

    @Test
    void convertItemToDto() {
        itemDto = null;
        itemDto = ItemDtoRowMapper.convertItemToDto(item);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertTrue((itemDto.getIsAvailable()));
        assertEquals(item.getOwnerId(), itemDto.getOwnerId());
        assertEquals(item.getOwnerId(), itemDto.getOwnerId());
    }

    @Test
    void convertDtoToItem() {
        item = null;
        item = ItemDtoRowMapper.convertDtoToItem(itemDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertTrue((item.getIsAvailable()));
        assertEquals(itemDto.getOwnerId(), item.getOwnerId());
        assertEquals(itemDto.getOwnerId(), item.getOwnerId());
    }
}
