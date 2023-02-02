package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemServiceImpl;

    @Autowired
    public ItemController(@Qualifier("ItemServiceRepos") ItemService itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping()
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody @Valid ItemDto itemDto) {
        return itemServiceImpl.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemServiceImpl.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        return itemServiceImpl.editItem(userId, itemId, itemDto);
    }

    @GetMapping()
    public List<ItemDto> getListOfItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(value = "from", required = false) Long from,
                                        @RequestParam(value = "size", required = false) Long size) {
        return itemServiceImpl.getListOfItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getListOfItemsBySearch(@RequestParam(name = "text") String text,
                                                @RequestParam(value = "from", required = false) Long from,
                                                @RequestParam(value = "size", required = false) Long size) {
        return itemServiceImpl.getListOfItemsBySearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody @Valid CommentDto commentDto) {
        return itemServiceImpl.addCommentToItem(userId, itemId, commentDto);
    }
}
