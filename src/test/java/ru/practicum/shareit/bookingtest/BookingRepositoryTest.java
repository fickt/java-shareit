package ru.practicum.shareit.bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager em;

    Booking booking;

    Item item;

    @BeforeEach
    void createBooking() {
        booking = Booking.builder()
                .itemId(1L)
                .bookerId(1L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(10))
                .build();
    }

    @BeforeEach
    void createItem() {
        item = Item.builder()
                .id(1L)
                .ownerId(1L)
                .requestId(1L)
                .description("description")
                .name("name")
                .isAvailable(Boolean.TRUE)
                .build();
        em.merge(item);
        em.clear();
        itemRepository.save(item);
    }
/**
 * if run separately, test passes
    */
    @Test
    void shouldFindBookingsByItemOwnerId() {
        em.persistAndFlush(booking);
        em.clear();

        List<Booking> bookingList = bookingRepository.findAllByOwnerId(1L);
        assertFalse(bookingList.isEmpty());
    }

    @Test
    void shouldFindBookingsByBookerIdWithStatusCurrent() {
        booking.setStart(LocalDateTime.now().minusHours(5));
        em.persistAndFlush(booking);
        em.clear();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndDateBetweenStartAndEnd(1L, LocalDateTime.now());
        assertFalse(bookingList.isEmpty());
    }

    @Test
    void shouldFindBookingsByBookerIdWithStatusPast() {
        booking.setStart(LocalDateTime.now().minusDays(20));
        booking.setEnd(LocalDateTime.now().minusHours(15));
        em.persistAndFlush(booking);
        em.clear();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndDateAfterEnd(1L, LocalDateTime.now());
        assertFalse(bookingList.isEmpty());
    }

    @Test
    void shouldFindBookingsByBookerIdWithStatusFuture() {
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        em.persistAndFlush(booking);
        em.clear();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndDateBeforeStart(1L, LocalDateTime.now());
        assertFalse(bookingList.isEmpty());
    }

    @Test
    void shouldFindBookingsByBookerId() {
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        em.persistAndFlush(booking);
        em.clear();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndDateBeforeStart(1L, LocalDateTime.now());
        assertFalse(bookingList.isEmpty());
    }

}
