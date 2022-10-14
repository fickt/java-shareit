package ru.practicum.shareit.item.comment.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(message = "comment should not be empty")
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;
}
