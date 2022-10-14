package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestDto {
    @NotBlank
    private String description;
}
