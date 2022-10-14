package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                @RequestBody RequestDto requestDto) {
        return requestClient.createRequest(requestDto, requestorId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return requestClient.getRequestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRangeOfRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "1") Long from,
                                                     @Positive @RequestParam(value = "size", defaultValue = "10") Long size) {
        return requestClient.getRangeOfRequests(from, size, userId);
    }
}
