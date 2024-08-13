package com.jetcab.service.booking;

import com.jetcab.persistence.BookingRepository;
import com.jetcab.service.booking.dto.BookingDTO;
import com.jetcab.service.booking.dto.BookingStatisticsDTO;
import com.jetcab.service.booking.dto.ModifyBookingDTO;
import com.jetcab.service.booking.exception.BookingNotFoundException;
import com.jetcab.service.booking.exception.BookingStatusChangeException;
import com.jetcab.service.booking.exception.CancelBookingException;
import com.jetcab.service.booking.exception.InvalidBookingStateException;
import com.jetcab.service.booking.exception.UpdateBookingException;
import com.jetcab.service.booking.mapper.BookingMapper;
import com.jetcab.service.booking.model.Booking;
import com.jetcab.service.booking.model.BookingStatus;
import com.jetcab.service.location.LocationService;
import com.jetcab.service.location.model.Location;
import com.jetcab.service.passenger.PassengerService;
import com.jetcab.service.taxi.TaxiService;
import com.jetcab.service.taxi.model.Taxi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.jetcab.service.booking.model.BookingStatus.CANCELLED;
import static com.jetcab.service.booking.model.BookingStatus.COMPLETED;
import static com.jetcab.service.booking.model.BookingStatus.CONFIRMED;
import static com.jetcab.service.booking.model.BookingStatus.PENDING;
import static com.jetcab.service.taxi.model.TaxiStatus.AVAILABLE;
import static com.jetcab.service.taxi.model.TaxiStatus.BOOKED;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final LocationService locationService;
    private final PassengerService passengerService;
    private final TaxiService taxiService;

    @Override
    @Transactional
    public BookingDTO createBooking(ModifyBookingDTO dto) {
        Booking booking = new Booking();
        booking.setStatus(PENDING);
        booking.setPassenger(passengerService.getById(dto.getPassengerId()));
        booking.setPickupLocation(locationService.findOrCreate(dto.getPickupLocation()));
        booking.setDropOffLocation(locationService.findOrCreate(dto.getDropOffLocation()));
        booking.setBookedAt(ZonedDateTime.now());

        Booking saved = bookingRepository.saveAndFlush(booking);
        return bookingMapper.map(saved);
    }

    @Override
    @Transactional
    public BookingDTO updateBooking(Long bookingId, ModifyBookingDTO dto) {
        Booking booking = bookingRepository.findBookingById(bookingId);

        if (booking.getStatus() == PENDING || booking.getStatus() == CONFIRMED) {
            Location pickupLocation = locationService.findOrCreate(dto.getPickupLocation());

            if (booking.getStatus() == CONFIRMED && !booking.getPickupLocation().getId().equals(pickupLocation.getId())) {
                throw new UpdateBookingException();
            }

            booking.setPickupLocation(pickupLocation);
            booking.setDropOffLocation(locationService.findOrCreate(dto.getDropOffLocation()));

            Booking updated = bookingRepository.saveAndFlush(booking);
            return bookingMapper.map(updated);
        }

        throw new UpdateBookingException();
    }

    @Override
    @Transactional
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findBookingById(bookingId);

        if (statusCannotBeChanged(booking.getStatus(), status)) {
            throw new BookingStatusChangeException();
        }

        booking.setStatus(status);
        Booking updated = bookingRepository.saveAndFlush(booking);
        return bookingMapper.map(updated);
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);

        if (statusCannotBeChanged(booking.getStatus(), CANCELLED)) {
            throw new CancelBookingException();
        }

        booking.setStatus(CANCELLED);
        Booking canceled = bookingRepository.saveAndFlush(booking);
        return bookingMapper.map(canceled);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingStatisticsDTO getBookingStatistics(ZonedDateTime from, ZonedDateTime to) {
        List<BookingStatus> bookings = bookingRepository.findAllBookingStatusBetween(from, to);
        Map<BookingStatus, Long> bookingStatusCountsMap = bookings.stream().collect(groupingBy(status -> status, counting()));

        return BookingStatisticsDTO.builder()
                .totalBookings(bookings.size())
                .inProgressBookings(bookingStatusCountsMap.getOrDefault(PENDING, 0L) + bookingStatusCountsMap.getOrDefault(CONFIRMED, 0L))
                .completedBookings(bookingStatusCountsMap.getOrDefault(COMPLETED, 0L))
                .cancelledBookings(bookingStatusCountsMap.getOrDefault(CANCELLED, 0L))
                .build();
    }

    @Override
    @Transactional
    public BookingDTO takeBooking(Long bookingId, Long taxiId) {
        Booking booking = bookingRepository.findBookingForUpdate(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (booking.getStatus() != PENDING) {
            throw new InvalidBookingStateException("exception.conflict.booking-not-available");
        }

        Taxi taxi = taxiService.findById(taxiId);
        taxi.setStatus(BOOKED);
        taxiService.update(taxi);

        booking.setStatus(CONFIRMED);
        booking.setTaxi(taxi);
        Booking updated = bookingRepository.saveAndFlush(booking);

        return bookingMapper.map(updated);
    }

    @Override
    @Transactional
    public BookingDTO completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException("exception.conflict.booking-cannot-be-completed");
        }

        booking.setStatus(COMPLETED);
        Booking updated = bookingRepository.saveAndFlush(booking);

        Taxi taxi = booking.getTaxi();

        if (taxi != null) {
            taxi.setStatus(AVAILABLE);
            taxiService.update(taxi);
        }

        return bookingMapper.map(updated);
    }

    public boolean statusCannotBeChanged(BookingStatus current, BookingStatus newStatus) {
        return switch (current) {
            case PENDING -> !(newStatus == CONFIRMED || newStatus == CANCELLED);
            case CONFIRMED -> !(newStatus == COMPLETED || newStatus == CANCELLED);
            default -> true; // COMPLETED and CANCELLED statuses cannot transition
        };
    }
}
