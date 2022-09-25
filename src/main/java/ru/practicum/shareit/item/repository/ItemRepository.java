package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Pageable pageable, Long userId);

    List<Item> findItemsByOwnerId(Long userId);

    List<Item> findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(Pageable pageable, String name, String description);

    List<Item> findItemsByNameOrDescriptionContainingIgnoreCaseAndIsAvailableTrue(String name, String description);

    Optional<Item> findFirstById(Long itemId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO REQUEST_ITEM_TABLE (REQUEST_ID, ITEM_ID) VALUES (:requestId, :userId)", nativeQuery = true)
    void saveToRequestAndItemIdTable(@Param("requestId") Long requestId, @Param("userId") Long userId);
}
