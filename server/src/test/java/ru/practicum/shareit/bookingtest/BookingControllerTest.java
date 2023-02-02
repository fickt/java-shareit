package ru.practicum.shareit.bookingtest;

import com.google.gson.Gson;


import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImplRepos;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.handler.ExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson = new Gson();

    @MockBean
    BookingServiceImplRepos bookingService;

    BookingDto bookingDto;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup()
                .setControllerAdvice(new ExceptionHandler())
                .build();
    }

    @BeforeEach
    void createItemAndBookingAndUser() {

        bookingDto = BookingDto.builder()
                .id(1L)
                .bookerId(1L)
                .itemId(1L)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldReturnBookingWithChangedStatus() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.changeStatusBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnListOfBookingsOfUser() throws Exception {
        when(bookingService.getAllBookingsOfUser(anyLong(), any(), anyLong(), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=all&from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.[0].itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.[0].status").value("WAITING"));
    }

    @Test
    void shouldReturnListOfBookingsOfItemOwner() throws Exception {
        when(bookingService.getAllBookingsOfItemsOfOwner(anyLong(), any(), anyLong(), anyLong()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner?state=all&from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.[0].itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.[0].status").value("WAITING"));
    }

    @Test
    void shouldThrowValidationExceptionBecauseNonExistentStatus() throws Exception {
        when(bookingService.getAllBookingsOfUser(anyLong(), any(), any(), any()))
                .thenThrow(new ValidationException("Unknown state: unsupported"));

        mockMvc.perform(get("/bookings?state=unsupported")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: unsupported"));
    }
}
