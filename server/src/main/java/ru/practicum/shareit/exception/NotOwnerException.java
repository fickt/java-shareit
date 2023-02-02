package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotOwnerException extends RuntimeException {

    public NotOwnerException(String message) {
        super(message);
    }
}
