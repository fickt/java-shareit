package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {
    @NotBlank(message = "comment should not be empty")
    private String text;
}
