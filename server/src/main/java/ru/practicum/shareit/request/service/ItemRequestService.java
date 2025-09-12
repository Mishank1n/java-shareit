package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(Long userId, ItemRequest request);

    List<ItemRequestDtoWithAnswers> getAllUserRequestsAndOrderByCreatedDesc(Long userId);

    List<ItemRequestDtoWithoutAnswers> getAllRequestsNotByUserAndSortedByCreatedDesc(Long userId);

    ItemRequestDtoWithAnswers getByIdWithAnswers(Long userId, Long requestId);

    ItemRequest getById(Long requestId);
}
