package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class InvalidDataConflictException extends RuntimeException {

        public InvalidDataConflictException(String message) {
            super(message);
        }
}
