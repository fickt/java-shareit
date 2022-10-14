package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {

    @Query(value = "SELECT OWNER_ID FROM ITEM_TABLE WHERE ID= :itemId", nativeQuery = true)
    Long findOwnerOfItem(@Param("itemId") Long itemId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    /**
     * to find current bookings
    */
    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date BETWEEN START_DATE AND END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateBetweenStartAndEnd(@Param("userId") Long userId, @Param("date") LocalDateTime date, Pageable pageable);

    /**
     * to find past bookings
     */
    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date > END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateAfterEnd(@Param("userId") Long userId, @Param("date") LocalDateTime date, Pageable pageable);

    /**
     * to find future bookings
     */
    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date < START_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateBeforeStart(@Param("userId") Long userId, @Param("date") LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStart(Long userId, Status status, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId ORDER BY START_DATE", nativeQuery = true)
    List<Booking> findAllByOwnerIdOrderByStartAsc(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date BETWEEN START_DATE AND END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBetweenStartAndEnd(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date > END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateAfterStart(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date < START_DATE OR :date = START_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBeforeStart(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId AND STATUS= UPPER(:status) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndStatusEqualsOrderByStart(@Param("ownerId") Long ownerId, @Param("status") String status, Pageable pageable);

   // --------------------------------------------------------------Unpageable:------------------------------------------------------------------------

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date BETWEEN START_DATE AND END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateBetweenStartAndEnd(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date > END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateAfterEnd(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE USER_ID= :userId AND :date < START_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAndDateBeforeStart(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStart(Long userId, Status status);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date BETWEEN START_DATE AND END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBetweenStartAndEnd(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date > END_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateAfterStart(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND :date < START_DATE OR :date = START_DATE ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBeforeStart(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId AND STATUS= UPPER(:status) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndStatusEqualsOrderByStart(@Param("ownerId") Long ownerId, @Param("status") String status);

    Booking findFirstByItemIdAndStartBefore(Long itemId, LocalDateTime date);

    Booking findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime date);

    Optional<Booking> findFirstByItemId(Long itemId);
}
