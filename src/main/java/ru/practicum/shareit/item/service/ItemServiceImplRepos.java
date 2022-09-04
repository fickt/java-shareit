package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.rowmapper.ItemDtoRowMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service("ItemServiceRepos")
public class ItemServiceImplRepos implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImplRepos(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(long userId, @Valid ItemDto itemDto) { //TODO add checkUser
        if (userRepository.existsById(userId)) {
            itemDto.setOwnerId(userId);
            Item item = itemRepository.save(ItemDtoRowMapper.convertDtoToItem(itemDto));
            return ItemDtoRowMapper.convertItemToDto(item);
        } else {
            throw new NotFoundException(String.format("User with ID: %s has not been found!", userId));
        }
    }

    @Override
    public ItemDto getItem(long itemId) { //TODO draft version with no validation
        if (itemRepository.findById(itemId).isPresent()) {
            return ItemDtoRowMapper.convertItemToDto(itemRepository.findById(itemId).get());
        } else {
            throw new NotFoundException(String.format("Item with ID: %s has not been found!", itemId));
        }
    }

    @Override
    public ItemDto editItem(long userId, long itemId, ItemDto itemDto) {
        Item item;
        if (itemRepository.findById(itemId).isPresent()) {
            item = itemRepository.findById(itemId).get();

            if (item.getOwnerId() != userId) {
                throw new NotOwnerException("You are not an owner of this item!");
            }

            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }

            if (itemDto.getIsAvailable() != null) {
                item.setIsAvailable(itemDto.getIsAvailable());
            }
            return ItemDtoRowMapper.convertItemToDto(itemRepository.save(item));
        } else {
            throw new NotFoundException(String.format("Item with ID: %s has not been found!", itemId));
        }
    }

    @Override
    public List<ItemDto> getListOfItems(long userId) {
        return ItemDtoRowMapper.convertListOfItemsToListOfDtoItems(itemRepository.findItemsByOwnerId(userId));
    }

    @Override
    public List<ItemDto> getListOfItemsBySearch(String text) {
        if(text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemDtoRowMapper
                .convertListOfItemsToListOfDtoItems(itemRepository
                        .findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(text,text));
    }
}
