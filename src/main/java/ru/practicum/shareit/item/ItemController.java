package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemServiceImpl;

    @Autowired
    public ItemController(ItemService itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping()
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestBody ItemDto itemDto) {
        return itemServiceImpl.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemServiceImpl.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        return itemServiceImpl.editItem(userId, itemId, itemDto);
    }

    @GetMapping()
    public List<ItemDto> getListOfItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemServiceImpl.getListOfItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getListOfItemsBySearch(@RequestParam(name = "text") String text) {
        return itemServiceImpl.getListOfItemsBySearch(text);
    }
}
