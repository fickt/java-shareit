package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO ITEM_COMMENT_TABLE (ITEM_ID, COMMENT_ID) VALUES (:itemId, :commentId)", nativeQuery = true)
    void saveItemIdAndCommentIdToItemCommentTable(@Param("itemId") Long itemId, @Param("commentId") Long commentId);

    List<Comment> findAllByItemId(Long itemId);

    Boolean existsByItemIdAndAuthorId(Long itemId, Long authorId);
}
