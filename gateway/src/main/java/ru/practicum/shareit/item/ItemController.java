package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto) {
       return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId,
                                           @RequestBody ItemDto itemDto) {
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getListOfItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(value = "from", required = false) Long from,
                                                 @Positive @RequestParam(value = "size", required = false) Long size) {

        return itemClient.getListOfItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getListOfItemsBySearch(@RequestParam(name = "text") String text,
                                                @PositiveOrZero @RequestParam(value = "from", required = false) Long from,
                                                @Positive @RequestParam(value = "size", required = false) Long size) {
        return itemClient.getListOfItemsBySearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDto commentDto) {
        return itemClient.addCommentToItem(userId, itemId, commentDto);
    }
}
