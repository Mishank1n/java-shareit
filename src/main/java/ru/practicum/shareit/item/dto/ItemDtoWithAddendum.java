package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithAddendum {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;

    public static ItemDtoWithAddendum toItemDtoWithAddendum(Item item, LocalDateTime last, LocalDateTime future, List<Comment> comments){
        return new ItemDtoWithAddendum(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner().getId(), last, future, comments.stream().map(CommentDto::toCommentDto).toList());
    }
}
