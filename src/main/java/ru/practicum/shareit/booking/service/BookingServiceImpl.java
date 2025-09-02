package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.models.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemServiceImpl itemService;

    @Override
    @Transactional
    public Booking create(Long bookerId, Booking booking) {
        if (booking.getStart().equals(booking.getEnd()) || booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Время начала бронирования не может быть позже времени окончания бронирования или сопадать ним");
        } if (booking.getStart().isBefore(LocalDateTime.now())){
            throw new ValidationException("Время начала бронирования не может быть в прошлом");
        } if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().equals(LocalDateTime.now())){
            throw new ValidationException("Время окончания бронирования не может быть в прошлом или настоящим");
        }
        User booker = userService.get(bookerId);
        Item item = itemService.getByIdWithoutSecondary(booking.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет должен быть доступен для бронирования");
        }
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        return repository.save(booking);
    }

    @Override
    public Booking get(Long bookingId, Long userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> {
            log.error("Бронирование с id = {} не найдено", bookingId);
            return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
        });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Просматривать бронированние может только пользователь, который отправил данный запрос, или владелец вещи");
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking responseToBooking(Long bookingId, boolean isApproved, Long userId) {
        Booking booking = repository.findByIdWithItemAndOwnerAndBooker(bookingId).orElseThrow(() -> {
            log.error("Бронирование с id = {} не найдено", bookingId);
            return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
        });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Ответить на бронирование может только владелец вещи");
        }
        if (isApproved) {
            repository.updateStatusToApprove(bookingId);
        } else {
            repository.updateStatusToRejected(bookingId);
        }
        repository.flush();
        entityManager.clear();
        return repository.findByIdWithItemAndOwnerAndBooker(bookingId).get();
    }

    @Override
    public List<Booking> getAllBookingsOfUser(Long userId, String state) {
        List<Booking> allBookingsOfUser = repository.findAllByBookerIdOrderByStartDate(userId);
        return filterWithState(allBookingsOfUser, state);
    }

    @Override
    public List<Booking> getAllBookingsOfItemOwner(Long userId, String state) {
        List<Booking> allBookingsOfItemOwner = repository.findAllByItemOwnerIdOrderByStartDate(userId);
        if (itemService.getAllUserItems(userId).isEmpty()){
            throw new NotFoundException(String.format("У пользователя с id = %d нет предметов для бронирования", userId));
        }
        if (allBookingsOfItemOwner.isEmpty()){
            throw new NotFoundException(String.format("Вещи пользователя с id = %d еще не бронировались", userId));
        }
        return filterWithState(allBookingsOfItemOwner, state);
    }

    private List<Booking> filterWithState (List<Booking> bookingList, String state){
        return switch (state) {
            case "CURRENT" ->
                    bookingList.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED)).filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())).toList();
            case "PAST" ->
                    bookingList.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED)).filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).toList();
            case "FUTURE" ->
                    bookingList.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED)).filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).toList();
            case "WAITING" ->
                    bookingList.stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).toList();
            case "REJECTED" ->
                    bookingList.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).toList();
            default -> bookingList;
        };
    }
}
