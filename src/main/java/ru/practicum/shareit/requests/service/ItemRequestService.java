package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto requestDto);

    List<ItemRequestDto> getAllRequests(Long requestorId);

    List<ItemRequestDto> getRangeOfRequests(Long from, Long size);

    ItemRequestDto getItemRequestById(Long requestId);
}
