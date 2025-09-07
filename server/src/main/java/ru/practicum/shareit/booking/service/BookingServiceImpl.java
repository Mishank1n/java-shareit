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
            log.error("Время начала бронирования не может быть позже времени окончания бронирования или совпадать с ним");
            throw new ValidationException("Время начала бронирования не может быть позже времени окончания бронирования или совпадать с ним");
        }
        User booker = userService.get(bookerId);
        Item item = itemService.getByIdWithoutSecondary(booking.getItemId());
        if (!item.getAvailable()) {
            log.error("Предмет с id = {} должен быть доступен для бронирования", item.getId());
            throw new ValidationException(String.format("Предмет с id = %d должен быть доступен для бронирования", item.getId()));
        }
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        log.info("Пользователь с id = {} создал бронирование вещи с id = {}", bookerId, booking.getItemId());
        return repository.save(booking);
    }

    @Override
    public Booking get(Long bookingId, Long userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> {
            log.error("Бронирование с id = {} не найдено", bookingId);
            return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
        });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Просматривать бронирование может только пользователь, который отправил данный запрос, или владелец вещи");
            throw new ValidationException("Просматривать бронирование может только пользователь, который отправил данный запрос, или владелец вещи");
        }
        log.info("Было получено бронирование с id = {}", bookingId);
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
            log.error("Ответить на бронирование может только владелец вещи");
            throw new ValidationException("Ответить на бронирование может только владелец вещи");
        }
        if (isApproved) {
            repository.updateStatusToApprove(bookingId);
        } else {
            repository.updateStatusToRejected(bookingId);
        }
        repository.flush();
        entityManager.clear();
        log.info("Пользователь с id = {} ответил на бронирование с id = {}", userId, bookingId);
        return repository.findByIdWithItemAndOwnerAndBooker(bookingId).get();
    }

    @Override
    public List<Booking> getAllBookingsOfUser(Long userId, String state) {
        User user = userService.get(userId);
        List<Booking> allBookingsOfUser = repository.findAllByBookerIdOrderByStartDate(userId);
        log.info("Были получены и возвращены все бронирования пользователя с id = {} с параметром поиска = {}", userId, state);
        return filterWithState(allBookingsOfUser, state);
    }

    @Override
    public List<Booking> getAllBookingsOfItemOwner(Long userId, String state) {
        List<Booking> allBookingsOfItemOwner = repository.findAllByItemOwnerIdOrderByStartDate(userId);
        if (itemService.getAllUserItems(userId).isEmpty()) {
            log.error("У пользователя с id = {} нет предметов", userId);
            throw new NotFoundException(String.format("У пользователя с id = %d нет предметов", userId));
        }
        if (allBookingsOfItemOwner.isEmpty()) {
            log.error("Вещи пользователя с id = {} еще не бронировались", userId);
            throw new NotFoundException(String.format("Вещи пользователя с id = %d еще не бронировались", userId));
        }
        log.info("Были получены и возвращены все бронирования вещей пользователя с id = {} с параметром поиска = {}", userId, state);
        return filterWithState(allBookingsOfItemOwner, state);
    }

    public List<Booking> filterWithState(List<Booking> bookingList, String state) {
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
