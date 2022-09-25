package ru.practicum.shareit.requests.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "REQUEST_TABLE")
public class ItemRequest {

    public ItemRequest() {

    }

    public ItemRequest(Long id, String description, Long requestorId, LocalDateTime created, List<Item> items) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "REQUESTOR_ID")
    private Long requestorId;
    @Column(name = "CREATED_AT")
    private LocalDateTime created;
    @OneToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "REQUEST_ITEM_TABLE",
            joinColumns = { @JoinColumn(name = "REQUEST_ID") },
            inverseJoinColumns = { @JoinColumn(name = "ITEM_ID") }
    )
    List<Item> items = new ArrayList<>();
}
