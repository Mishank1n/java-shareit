package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.item.owner JOIN FETCH b.booker WHERE b.id = :id")
    Optional<Booking> findByIdWithItemAndOwnerAndBooker(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE bookings SET status = 'APPROVED' WHERE id = :bookingId")
    void updateStatusToApprove(@Param("bookingId") Long bookingId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE bookings SET status = 'REJECTED' WHERE id = :bookingId")
    void updateStatusToRejected(@Param("bookingId") Long bookingId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.item.owner JOIN FETCH b.booker WHERE b.booker.id = :id ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdOrderByStartDate(@Param("id") Long id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.item.owner JOIN FETCH b.booker WHERE b.item.owner.id = :id ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdOrderByStartDate(@Param("id") Long id);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start < :now ORDER BY b.start DESC LIMIT 1")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC LIMIT 1")
    Optional<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b.booker FROM Booking b WHERE b.status = 'APPROVED' AND b.end < :now AND b.item.id = :itemId")
    List<User> findAllBookerWhichTakeItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}