package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Pageable pageable, long userId);

    List<Item> findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(Pageable pageable,String name,String description);

    Optional<Item> findFirstById(Long itemId);
}
