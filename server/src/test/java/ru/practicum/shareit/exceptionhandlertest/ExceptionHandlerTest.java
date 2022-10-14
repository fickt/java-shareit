package ru.practicum.shareit.exceptionhandlertest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.InvalidDataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.handler.ExceptionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExceptionHandlerTest {

    ExceptionHandler handler = new ExceptionHandler();

    @Test
    void shouldHandleInvalidDataConflictExceptionAndReturn409Conflict() {
        ResponseEntity<String> response = handler.handleInvalidDataConflictException(new InvalidDataConflictException("error"));
        assertEquals(409, response.getStatusCode().value());
        assertEquals("error", response.getBody());
    }

    @Test
    void shouldHandleUserNotFoundExceptionAndReturn404NotFound() {
        ResponseEntity<String> response = handler.handleUserNotFoundException(new NotFoundException("error"));
        assertEquals(404, response.getStatusCode().value());
        assertEquals("error", response.getBody());
    }

    @Test
    void shouldHandleNotOwnerExceptionAndReturn() {
        ResponseEntity<String> response = handler.handleNotOwnerException(new NotOwnerException("error"));
        assertEquals(403, response.getStatusCode().value());
        assertEquals("error", response.getBody());
    }
}
