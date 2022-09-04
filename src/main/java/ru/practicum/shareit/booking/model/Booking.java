package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "BOOKING_TABLE")
public class Booking {

    public Booking(Long id, LocalDateTime start, LocalDateTime end, User booker, Item item, Long itemId, Long userId, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.userId = userId;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    public Booking() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    @Column(name = "END_DATE")
    private LocalDateTime end;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "USER_ID")
    private Long userId;
    @ManyToOne
    @JoinTable(name = "BOOKING_USER_TABLE",
            joinColumns = {@JoinColumn(name = "BOOKING_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID")}
    )
    private User booker;
    @OneToOne
    @JoinTable(name = "ITEM_BOOKING_TABLE",
            joinColumns = {@JoinColumn(name = "BOOKING_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ITEM_ID")}
    )
    private Item item;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;
}
