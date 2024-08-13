package com.jetcab.persistence;

import com.jetcab.service.booking.model.Booking;
import com.jetcab.service.booking.model.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jetcab.service.booking.exception.BookingNotFoundException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Booking findBookingById(Long bookingId) {
        return findById(bookingId).orElseThrow(BookingNotFoundException::new);
    }

    @Query(value = "select b.status from Booking b where b.bookedAt between :from and :to")
    List<BookingStatus> findAllBookingStatusBetween(@Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
    Optional<Booking> findBookingForUpdate(@Param("bookingId") Long bookingId);
}
