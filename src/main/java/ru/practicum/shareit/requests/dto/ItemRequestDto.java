package ru.practicum.shareit.requests.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
