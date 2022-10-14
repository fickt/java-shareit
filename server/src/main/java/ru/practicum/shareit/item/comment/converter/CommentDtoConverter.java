package ru.practicum.shareit.item.comment.converter;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;

public class CommentDtoConverter {
    public static Comment convertDtoToComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .authorId(commentDto.getAuthorId())
                .itemId(commentDto.getItemId())
                .created(commentDto.getCreated())
                .authorName(commentDto.getAuthorName())
                .build();
    }

    public static CommentDto convertCommentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthorId())
                .itemId(comment.getItemId())
                .authorName(comment.getAuthorName())
                .created(comment.getCreated())
                .build();
    }
}
