package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleValidationException(ValidationException e) {
        return new ResponseEntity(new ValidationErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
