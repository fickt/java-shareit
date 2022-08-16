package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    private Long ownerId;
    @NotNull(message = "name should not be empty")
    @NotBlank(message = "name should not be empty")
    private String name;
    @NotNull(message = "description should not be empty")
    @NotBlank(message = "description should not be empty")
    private String description;
    @NotNull(message = "please set availability")
    @JsonProperty("available")
    private Boolean isAvailable;
}
