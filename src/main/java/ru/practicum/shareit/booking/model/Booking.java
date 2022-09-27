package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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

    public Booking(Long id, LocalDateTime start, LocalDateTime end, Long bookerId, Long itemId, User booker, Item item, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    public Booking() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "START_DATE")
    private LocalDateTime start;
    @JsonFormat(pattern = "uuuu-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "END_DATE")
    private LocalDateTime end;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "USER_ID")
    private Long bookerId;
    @NotFound(
            action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User booker;
    @NotFound(
            action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", insertable = false, updatable = false)
    private Item item;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;
}
