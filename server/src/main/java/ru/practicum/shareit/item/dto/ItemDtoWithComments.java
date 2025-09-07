package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithComments {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    List<CommentDto> comments;

    public static ItemDtoWithComments toItemDtoWithComments(Item item, List<Comment> comments) {
        return new ItemDtoWithComments(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner().getId(), comments.stream().map(CommentDto::toCommentDto).toList());
    }
}