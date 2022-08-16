package ru.practicum.shareit.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestorId;
    private String created;
}
