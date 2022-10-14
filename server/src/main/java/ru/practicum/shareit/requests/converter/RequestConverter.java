package ru.practicum.shareit.requests.converter;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class RequestConverter {

    public static ItemRequestDto convertRequestToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestorId())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }

    public static ItemRequest convertDtoToRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestorId(itemRequestDto.getRequestorId())
                .created(itemRequestDto.getCreated())
                .build();

    }

    public static List<ItemRequestDto> convertRequestListToDto(List<ItemRequest> requestList) {
        return requestList.stream()
                .map(RequestConverter::convertRequestToDto)
                .collect(Collectors.toList());
    }
}
