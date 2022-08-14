package ru.practicum.shareit.item.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ItemDtoRowMapper implements RowMapper<ItemDto>{

    @Override
    public ItemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemDto itemDto = ItemDto.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .isAvailable(rs.getBoolean("IS_AVAILABLE"))
                .ownerId(rs.getLong("OWNER_ID"))
                .build();
        return itemDto;
    }


    public static ItemDto mapRow(Map<String, Object> map) {
        ItemDto itemDto = ItemDto.builder()
                .id((Long) map.get("ID"))
                .name((String) map.get("NAME"))
                .description((String) map.get("DESCRIPTION"))
                .isAvailable((Boolean) map.get("IS_AVAILABLE"))
                .ownerId((Long) map.get("OWNER_ID"))
                .build();
        return itemDto;
    }

}