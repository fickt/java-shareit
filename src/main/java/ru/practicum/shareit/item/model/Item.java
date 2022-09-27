package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import ru.practicum.shareit.item.comment.model.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "ITEM_TABLE")
public class Item {

    public Item() {

    }

    public Item(Long id, Long ownerId, String name, String description, Boolean isAvailable, Long requestId, List<Comment> comments) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.requestId = requestId;
        this.comments = comments;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "OWNER_ID")
    private Long ownerId;
    @NotBlank(message = "name should not be empty")
    private String name;
    @NotBlank(message = "description should not be empty")
    private String description;
    @NotNull(message = "please set availability")
    @JsonProperty("available")
    @Column(name = "IS_AVAILABLE")
    private Boolean isAvailable;
    @Column(name = "REQUEST_ID")
    private Long requestId;
    @OneToMany(mappedBy = "item")
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

}
