package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
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
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                            @RequestBody @Valid ItemRequestDto requestDto) {
        return itemRequestService.createItemRequest(requestDto, requestorId);
    }


    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getAllRequests(requestorId);
    }

    /**
     * To get range of requests of all users;
     * {from} = initial index, {size} = last index;
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getRangeOfRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "from", required = false) Long from,
                                                   @RequestParam(value = "size", required = false) Long size) {
        return itemRequestService.getRangeOfRequests(from, size, userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}

