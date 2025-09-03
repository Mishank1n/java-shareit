package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{

    private ItemRequestRepository repository;

    private ItemService itemService;

    private UserService userService;


    @Override
    @Transactional
    public ItemRequest create(Long userId, ItemRequest request) {
        User requestor = userService.get(userId);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return repository.save(request);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getAllUserRequestsAndOrderByCreatedDesc(Long userId) {
        User requestor = userService.get(userId);
        List<ItemRequest> allUserRequests = repository.findByRequestorIdOrderByCreatedDesc(userId);
        if (allUserRequests.isEmpty()){
            return List.of();
        }
        return allUserRequests.stream().map(itemRequest -> ItemRequestDtoWithAnswers.toItemRequestDtoWithAnswers(itemRequest, itemService.getAllItemsWhichAreAnswerOnRequest(itemRequest.getId()))).toList();
    }

    @Override
    public List<ItemRequestDtoWithoutAnswers> getAllRequestsNotByUserAndSortedByCreatedDesc(Long userId) {
        User requestor = userService.get(userId);
        List<ItemRequest> allRequestNotUser = repository.findByRequestorIdNotOrderByCreatedDesc(userId);
        if (allRequestNotUser.isEmpty()){
            return List.of();
        }
        return allRequestNotUser.stream().map(ItemRequestDtoWithoutAnswers::toItemRequestDtoWithoutAnswers).toList();
    }

    @Override
    public ItemRequestDtoWithAnswers getByIdWithAnswers(Long userId, Long requestId) {
        User requestor = userService.get(userId);
        ItemRequest request = repository.findById(requestId).orElseThrow(()->{
            return new NotFoundException(String.format("Запрос с id = %d не найден", requestId));
        });
        return ItemRequestDtoWithAnswers.toItemRequestDtoWithAnswers(request, itemService.getAllItemsWhichAreAnswerOnRequest(requestId));
    }

    @Override
    public ItemRequest getById(Long requestId){
        return repository.findById(requestId).orElseThrow(()->{
            return new NotFoundException(String.format("Запрос с id = %d не найден", requestId));
        });
    }
}
