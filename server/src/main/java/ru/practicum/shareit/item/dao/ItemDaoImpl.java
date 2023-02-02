package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private static final String SQL_ADD_ITEM = "INSERT INTO ITEM_TABLE (NAME, DESCRIPTION, IS_AVAILABLE, OWNER_ID) " +
            "VALUES(?,?,?,?)";

    private static final String SQL_GET_ITEM = "SELECT * FROM ITEM_TABLE WHERE ID=?";

    private static final String SQL_UPDATE_NAME_OF_ITEM = "UPDATE ITEM_TABLE SET NAME=? WHERE ID=?";

    private static final String SQL_UPDATE_DESCRIPTION_OF_ITEM = "UPDATE ITEM_TABLE SET DESCRIPTION=? WHERE ID=?";

    private static final String SQL_UPDATE_AVAILABILITY_OF_ITEM = "UPDATE ITEM_TABLE SET IS_AVAILABLE=? WHERE ID=?";

    private static final String SQL_GET_LIST_OF_ITEMS = "SELECT * FROM ITEM_TABLE WHERE OWNER_ID=?";

    private static final String SQL_GET_LIST_OF_ITEMS_BY_SEARCH = "SELECT * FROM ITEM_TABLE WHERE LOWER(NAME) LIKE ? " +
            "OR LOWER(DESCRIPTION) LIKE ? AND IS_AVAILABLE=true";

    private static final String SQL_GET_OWNER_OF_ITEM = "SELECT OWNER_ID FROM ITEM_TABLE WHERE ID=?";

    private static final String SQL_CHECK_USER_IF_EXISTS = "SELECT COUNT(NAME) FROM USER_TABLE WHERE ID=?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ItemDto addItem(Long userId, @Valid Item item) {
        Optional.ofNullable(jdbcTemplate.queryForObject(SQL_CHECK_USER_IF_EXISTS, Long.class, userId))
                .orElseThrow(() -> new NotFoundException("User with ID:" + userId + "has not been found!"));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_ITEM, new String[]{"ID"});
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setBoolean(3, item.getIsAvailable());
            stmt.setLong(4, userId);
            return stmt;
        }, keyHolder);

        Number itemIdNumber = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new NotFoundException(String.format("A problem occurred saving item: %s in DB", item.getName())));

        return getItem(itemIdNumber.longValue());
    }


    @Override
    public ItemDto getItem(Long itemId) {
        return jdbcTemplate.queryForObject(SQL_GET_ITEM, new ItemDtoRowMapper(), itemId);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        Long ownerId = jdbcTemplate.queryForObject(SQL_GET_OWNER_OF_ITEM, Long.class, itemId);
        if (userId.equals(ownerId)) {
            if (itemDto.getName() != null) {
                jdbcTemplate.update(SQL_UPDATE_NAME_OF_ITEM, itemDto.getName(), itemId);
            }

            if (itemDto.getDescription() != null) {
                jdbcTemplate.update(SQL_UPDATE_DESCRIPTION_OF_ITEM, itemDto.getDescription(), itemId);
            }

            if (itemDto.getIsAvailable() != null) {
                jdbcTemplate.update(SQL_UPDATE_AVAILABILITY_OF_ITEM, itemDto.getIsAvailable(), itemId);
            }
            return getItem(itemId);
        } else {
            log.warn("user with ID: " + userId + " is not an owner of item with ID " + itemId);
            throw new NotFoundException("You are not an owner of this item");
        }
    }

    @Override
    public List<ItemDto> getListOfItems(Long userId) {
        List<ItemDto> listOfItemDto = new ArrayList<>();
        List<Map<String, Object>> mapOfItems = jdbcTemplate.queryForList(SQL_GET_LIST_OF_ITEMS, userId);

        for (Map<String, Object> map : mapOfItems) {
            listOfItemDto.add(ItemDtoRowMapper.mapRow(map));
        }
        return listOfItemDto;
    }

    @Override
    public List<ItemDto> getListOfItemsBySearch(String text) {
        List<ItemDto> listOfItemDto = new ArrayList<>();
        if (!text.isBlank()) {
            text = "%" + text.toLowerCase() + "%";
            List<Map<String, Object>> mapOfItems = jdbcTemplate.queryForList(SQL_GET_LIST_OF_ITEMS_BY_SEARCH, text, text);

            for (Map<String, Object> map : mapOfItems) {
                listOfItemDto.add(ItemDtoRowMapper.mapRow(map));
            }
        }
        return listOfItemDto;
    }
}
