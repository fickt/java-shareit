package ru.practicum.shareit.itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.InvalidDataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemDaoTest {

    @Autowired
    ItemDao itemDao;

    Item item;
    @BeforeEach
    void createItem() {
        item = Item.builder()
                .id(1L)
                .name("Item")
                .ownerId(1L)
                .description("Just an item")
                .isAvailable(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    @Test
    void shouldAddItem() {
        ItemDto itemResponse = itemDao.addItem(1L, item);
        assertNotNull(itemResponse);
    }

    @Test
    void shouldEditItem() {
        itemDao.addItem(1L, item);
        ItemDto itemDtoToEdit =  ItemDto.builder()
                .name("editedName")
                .description("editedDescription")
                .isAvailable(Boolean.FALSE)
                .build();
       ItemDto itemEdited = itemDao.editItem(1L, 1L, itemDtoToEdit);
       assertEquals("editedName", itemEdited.getName());
       assertEquals("editedDescription", itemEdited.getDescription());
       assertFalse(itemEdited.getIsAvailable());
    }

    @Test
    void shouldThrowExceptionNotOwnerTriesToEditItem() {
        itemDao.addItem(1L, item);
        ItemDto itemDtoToEdit =  ItemDto.builder()
                .name("editedName")
                .description("editedDescription")
                .isAvailable(Boolean.FALSE)
                .build();

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> itemDao.editItem(2L, 1L, itemDtoToEdit),
                "You are not an owner of this item"
                );
        assertEquals("You are not an owner of this item", thrown.getMessage());
    }

    @Test
    void shouldGetListOfItemsByUserId() {
        itemDao.addItem(1L, item);
        List<ItemDto> itemDtoList = itemDao.getListOfItems(1L);
        assertFalse(itemDtoList.isEmpty());
    }

    @Test
    void shouldGetListOfItemsByTextSearch() {
        itemDao.addItem(1L, item);
        List<ItemDto> itemDtoList = itemDao.getListOfItemsBySearch("item");
        assertFalse(itemDtoList.isEmpty());
    }
}
