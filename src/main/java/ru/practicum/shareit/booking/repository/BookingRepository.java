package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT OWNER_ID FROM ITEM_TABLE WHERE ID= :itemId", nativeQuery = true)
    Long findOwnerOfItem(@Param("itemId") Long itemId);

    List<Booking> findAllByUserId(Long userId);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE ID= :userId AND (:date BETWEEN START_DATE AND END_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByUserIdAndDateBetweenStartAndEnd(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE ID= :userId AND (:date > END_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByUserIdAndDateAfterEnd(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM BOOKING_TABLE WHERE ID= :userId AND (:date < START_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByUserIdAndDateBeforeStart(@Param("userId") Long userId, @Param("date") LocalDate date);

    List<Booking> findAllByUserIdAndStatusEqualsOrderByStart(@Param("userId") Long userId, Status status);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID " +
            "WHERE OWNER_ID= :ownerId ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND (:date BETWEEN START_DATE AND END_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBetweenStartAndEnd(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND (:date > END_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateAfterEnd(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND (:date < START_DATE) ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndDateBeforeStart(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM BOOKING_TABLE JOIN ITEM_TABLE ON BOOKING_TABLE.ITEM_ID = ITEM_TABLE.ID  " +
            "WHERE OWNER_ID= :ownerId AND STATUS= :status ORDER BY START_DATE DESC", nativeQuery = true)
    List<Booking> findAllByOwnerIdAndStatusEqualsOrderByStart(@Param("ownerId") Long ownerId, @Param("status") Status status);
}
