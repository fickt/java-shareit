package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;
    private Long ownerId;
    @NotBlank(message = "name should not be empty")
    private String name;
    @NotBlank(message = "description should not be empty")
    private String description;
    @NotNull(message = "please set availability")
    @JsonProperty("available")
    private Boolean isAvailable;
    private List<Comment> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Long requestId;
}
