package ru.practicum.shareit.requesttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
public class RequestJsonTest {

    ItemRequest request;

    ItemRequestDto requestDto;

    @BeforeEach
    void createRequest() {
        request = ItemRequest.builder()
                .id(1L)
                .requestorId(1L)
                .created(LocalDateTime.of(2011,11,11,11,11))
                .description("description")
                .build();
    }

    @BeforeEach
    void createRequestDto() {
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .requestorId(1L)
                .created(LocalDateTime.of(2011,11,11,11,11))
                .description("description")
                .build();
    }

    @Test
    void convertRequestToDto() {
        requestDto = null;
        requestDto = RequestConverter.convertRequestToDto(request);
        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getRequestorId(), requestDto.getRequestorId());
        assertEquals(request.getCreated(), requestDto.getCreated());
        assertEquals(request.getDescription(), requestDto.getDescription());
    }

    @Test
    void convertDtoToRequest() {
        request = null;
        request = RequestConverter.convertDtoToRequest(requestDto);
        assertNull(request.getId());                                //only database can assign value to ID
        assertEquals(requestDto.getRequestorId(), request.getRequestorId());
        assertEquals(requestDto.getCreated(), request.getCreated());
        assertEquals(requestDto.getDescription(), request.getDescription());
    }
}
