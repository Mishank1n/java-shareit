package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private ItemRequestService service;

    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDtoWithoutAnswers create(@RequestHeader(name = userIdHeader) Long userId, @RequestBody ItemRequest itemRequest) {
        return ItemRequestDtoWithoutAnswers.toItemRequestDtoWithoutAnswers(service.create(userId, itemRequest));
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getAllUserItemRequestsAndOrderByCreatedDesc(@RequestHeader(name = userIdHeader) Long userId) {
        return service.getAllUserRequestsAndOrderByCreatedDesc(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithoutAnswers> getAllRequestsNotByUserAndOrderByCreatedDesc(@RequestHeader(name = userIdHeader) Long userId) {
        return service.getAllRequestsNotByUserAndSortedByCreatedDesc(userId);
    }

    @GetMapping("/{request-id}")
    public ItemRequestDtoWithAnswers getByIdWithAnswers(@RequestHeader(name = userIdHeader) Long userId, @PathVariable("request-id") Long requestId) {
        return service.getByIdWithAnswers(userId, requestId);
    }
}
