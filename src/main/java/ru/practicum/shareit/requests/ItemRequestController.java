package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(ItemRequestDto requestDto) {
        return itemRequestService.createItemRequest(requestDto);
    }


    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getAllRequests(requestorId);
    }

/**
 * To get range of requests of all users,
 * where {from} = initial index, {size} = last index
 */
    @GetMapping("/all")
    public List<ItemRequestDto> getRangeOfRequests(@RequestParam(value = "from", required = false) Long from,
                                                   @RequestParam(value = "size", required = false) Long size) {
        return itemRequestService.getRangeOfRequests(from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}

