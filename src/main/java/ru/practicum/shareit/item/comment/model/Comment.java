package ru.practicum.shareit.item.comment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "COMMENT_TABLE")
public class Comment {
    public Comment() {

    }

    public Comment(Long id, String text, Long itemId, Long authorId, String authorName, LocalDateTime created , Item item) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorId = authorId;
        this.item = item;
        this.authorName = authorName;
        this.created = created;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "AUTHOR_ID")
    private Long authorId;
    @Column(name = "AUTHOR_NAME")
    private String authorName;
    @Column(name = "CREATED")
    @DateTimeFormat(pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", insertable = false, updatable = false)
    @JsonBackReference
    private Item item;
}
