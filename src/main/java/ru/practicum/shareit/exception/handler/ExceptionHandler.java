package ru.practicum.shareit.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleValidationException(ValidationException e) {
        return new ResponseEntity(new ValidationErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<String> handleInvalidDataConflictException(InvalidDataConflictException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<String> handleUserNotFoundException(NotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<String> handleNotOwnerException(NotOwnerException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
