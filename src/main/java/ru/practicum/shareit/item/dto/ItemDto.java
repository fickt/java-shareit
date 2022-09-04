package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    public ItemDto() {

    }

    public ItemDto(Long id, Long ownerId, String name, String description, Boolean isAvailable, Long requestId) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.requestId = requestId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "OWNER_ID")
    private Long ownerId;
    @NotBlank(message = "name should not be empty")
    private String name;
    @NotBlank(message = "description should not be empty")
    private String description;
    @NotNull(message = "please set availability")
    @JsonProperty("available")
    @Column(name = "IS_AVAILABLE")
    private Boolean isAvailable;
    // @Column(name="REQUEST_ID")
    private Long requestId;
}
