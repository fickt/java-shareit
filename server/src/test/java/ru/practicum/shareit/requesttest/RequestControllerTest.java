package ru.practicum.shareit.requesttest;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson = new Gson();

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void createRequest() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestorId(1L)
                .description("text")
                .build();
    }

    @Test
    void shouldCreateRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()));
    }

    @Test
    void shouldReturnRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()));
    }

    @Test
    void shouldReturnListOfRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].requestorId").value(itemRequestDto.getRequestorId()));
    }

    @Test
    void shouldReturnRangeOfRequests() throws Exception {
        when(itemRequestService.getRangeOfRequests(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].requestorId").value(itemRequestDto.getRequestorId()));
    }
}
