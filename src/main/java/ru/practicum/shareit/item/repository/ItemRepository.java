package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long userId);

    List<Item> findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(String name,String description);

    Optional<Item> findFirstById(Long itemId);
}
