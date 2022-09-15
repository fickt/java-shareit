package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ValidationErrorResponse {
    @JsonProperty("error")
    private final String errorMessage;
}
