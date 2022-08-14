package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * // TODO .
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
}
