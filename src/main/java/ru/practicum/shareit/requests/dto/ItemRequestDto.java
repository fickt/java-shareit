package ru.practicum.shareit.requests.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private Long requestorId;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private List<Item> items;
}
