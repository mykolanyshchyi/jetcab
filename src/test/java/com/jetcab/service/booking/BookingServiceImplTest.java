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
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.location.model.Location;
import com.jetcab.service.passenger.PassengerService;
import com.jetcab.service.passenger.exception.PassengerNotFoundException;
import com.jetcab.service.passenger.model.Passenger;
import com.jetcab.service.taxi.TaxiService;
import com.jetcab.service.taxi.model.Taxi;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.jetcab.service.booking.model.BookingStatus.CANCELLED;
import static com.jetcab.service.booking.model.BookingStatus.COMPLETED;
import static com.jetcab.service.booking.model.BookingStatus.CONFIRMED;
import static com.jetcab.service.booking.model.BookingStatus.PENDING;
import static com.jetcab.service.taxi.model.TaxiStatus.AVAILABLE;
import static com.jetcab.service.taxi.model.TaxiStatus.BOOKED;
import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final Long BOOKING_ID = 345346L;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private LocationService locationService;

    @Mock
    private PassengerService passengerService;

    @Mock
    private TaxiService taxiService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @Captor
    private ArgumentCaptor<Taxi> taxiCaptor;

    @Test
    void getBookingStatistics() {
        BookingStatisticsDTO expected = BookingStatisticsDTO.builder()
                .totalBookings(6)
                .inProgressBookings(2)
                .completedBookings(3)
                .cancelledBookings(1)
                .build();
        List<BookingStatus> bookings = List.of(PENDING, CONFIRMED, COMPLETED, COMPLETED, CANCELLED, COMPLETED);

        when(bookingRepository.findAllBookingStatusBetween(any(), any())).thenReturn(bookings);

        BookingStatisticsDTO actual = bookingService.getBookingStatistics(now().minusDays(1), now());

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Nested
    class CreateBooking {
        @Test
        void shouldThrowPassengerNotFoundException() {
            when(passengerService.getById(anyLong())).thenThrow(new PassengerNotFoundException());
            assertThrows(PassengerNotFoundException.class, () -> bookingService.createBooking(createBookingDto()));

            verify(passengerService).getById(anyLong());
            verifyNoInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
            verifyNoInteractions(locationService);
        }

        @Test
        void shouldSuccessfullyCreate() {
            when(passengerService.getById(anyLong())).thenReturn(new Passenger());
            when(locationService.findOrCreate(any(ModifyLocationDTO.class))).thenReturn(new Location());
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(new Booking());
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            bookingService.createBooking(createBookingDto());

            verify(passengerService).getById(anyLong());
            verify(locationService, times(2)).findOrCreate(any(ModifyLocationDTO.class));
            verify(bookingRepository).saveAndFlush(bookingCaptor.capture());
            verify(bookingMapper).map(any(Booking.class));

            Booking booking = bookingCaptor.getValue();
            assertNotNull(booking);
            assertEquals(PENDING, booking.getStatus());
            assertNotNull(booking.getPassenger());
            assertNotNull(booking.getPickupLocation());
            assertNotNull(booking.getDropOffLocation());
            assertNotNull(booking.getBookedAt());
        }
    }

    @Nested
    class UpdateBooking {

        @Test
        void shouldThrowBookingNotFoundException() {
            when(bookingRepository.findBookingById(anyLong())).thenThrow(new BookingNotFoundException());

            assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking(BOOKING_ID, createBookingDto()));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(locationService);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowUpdateBookingExceptionAsBookingStatusIsNotPendingOrConfirmed() {
            Booking booking = new Booking();
            booking.setStatus(COMPLETED);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);

            assertThrows(UpdateBookingException.class, () -> bookingService.updateBooking(BOOKING_ID, createBookingDto()));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(locationService);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowUpdateBookingExceptionAsBookingStatusIsConfirmedAndPickupLocationIsDifferent() {
            Location originalPickupLocation = createLocation(4356d, 521d);
            originalPickupLocation.setId(23L);
            Location updatedPickupLocation = createLocation(4156d, 8234d);
            updatedPickupLocation.setId(56L);

            Booking booking = new Booking();
            booking.setStatus(CONFIRMED);
            booking.setPickupLocation(originalPickupLocation);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);
            when(locationService.findOrCreate(any(ModifyLocationDTO.class))).thenReturn(updatedPickupLocation);

            assertThrows(UpdateBookingException.class, () -> bookingService.updateBooking(BOOKING_ID, createBookingDto()));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verify(locationService).findOrCreate(any(ModifyLocationDTO.class));
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldUpdateSuccessfully() {
            Location originalPickupLocation = createLocation(4356d, 521d);
            Location updatedPickupLocation = createLocation(4156d, 8234d);

            Booking booking = new Booking();
            booking.setStatus(PENDING);
            booking.setPickupLocation(originalPickupLocation);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);
            when(locationService.findOrCreate(any(ModifyLocationDTO.class))).thenReturn(updatedPickupLocation);
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            bookingService.updateBooking(BOOKING_ID, createBookingDto());

            verify(bookingRepository).findBookingById(anyLong());
            verify(bookingRepository).saveAndFlush(any(Booking.class));
            verify(locationService, times(2)).findOrCreate(any(ModifyLocationDTO.class));
            verify(bookingMapper).map(any(Booking.class));
        }
    }

    @Nested
    class UpdateBookingStatus {
        @Test
        void shouldThrowBookingNotFoundException() {
            when(bookingRepository.findBookingById(anyLong())).thenThrow(new BookingNotFoundException());

            assertThrows(BookingNotFoundException.class, () -> bookingService.updateBookingStatus(BOOKING_ID, CONFIRMED));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowBookingStatusChangeException() {
            Booking booking = new Booking();
            booking.setStatus(COMPLETED);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);

            assertThrows(BookingStatusChangeException.class, () -> bookingService.updateBookingStatus(BOOKING_ID, CONFIRMED));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldUpdateBookingStatusSuccessfully() {
            Booking booking = new Booking();
            booking.setStatus(CONFIRMED);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            BookingDTO result = bookingService.updateBookingStatus(BOOKING_ID, COMPLETED);

            assertNotNull(result);
            verify(bookingRepository).findBookingById(anyLong());
            verify(bookingRepository).saveAndFlush(bookingCaptor.capture());
            verify(bookingMapper).map(any(Booking.class));

            Booking captoredBooking = bookingCaptor.getValue();
            assertEquals(COMPLETED, captoredBooking.getStatus());
        }
    }

    @Nested
    class CancelBooking {

        @Test
        void shouldThrowBookingNotFoundException() {
            when(bookingRepository.findBookingById(anyLong())).thenThrow(new BookingNotFoundException());

            assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(BOOKING_ID));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowCancelBookingExceptionAsZBookingCannotBeCanceled() {
            Booking booking = new Booking();
            booking.setStatus(COMPLETED);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);

            assertThrows(CancelBookingException.class, () -> bookingService.cancelBooking(BOOKING_ID));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldCancelBookingSuccessfully() {
            Booking booking = new Booking();
            booking.setStatus(CONFIRMED);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            BookingDTO result = bookingService.cancelBooking(BOOKING_ID);

            assertNotNull(result);
            verify(bookingRepository).findBookingById(anyLong());
            verify(bookingRepository).saveAndFlush(bookingCaptor.capture());
            verify(bookingMapper).map(any(Booking.class));

            Booking captoredBooking = bookingCaptor.getValue();
            assertEquals(CANCELLED, captoredBooking.getStatus());
        }
    }

    @Nested
    class TakeBooking {

        @Test
        void shouldThrowBookingNotFoundException() {
            when(bookingRepository.findBookingForUpdate(anyLong())).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> bookingService.takeBooking(BOOKING_ID, 234L));

            verify(bookingRepository).findBookingForUpdate(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(taxiService);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowInvalidBookingStateExceptionAsBookingNotAvailableAnymore() {
            Booking booking = new Booking();
            booking.setStatus(CONFIRMED);
            when(bookingRepository.findBookingForUpdate(anyLong())).thenReturn(Optional.of(booking));

            InvalidBookingStateException exception = assertThrows(InvalidBookingStateException.class, () -> bookingService.takeBooking(BOOKING_ID, 23L));
            assertEquals("exception.conflict.booking-not-available", exception.getMessage());

            verify(bookingRepository).findBookingForUpdate(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldTakeBookingSuccessfully() {
            Booking booking = new Booking();
            booking.setStatus(PENDING);
            Taxi taxi = new Taxi();
            taxi.setStatus(AVAILABLE);
            when(bookingRepository.findBookingForUpdate(anyLong())).thenReturn(Optional.of(booking));
            when(taxiService.findById(anyLong())).thenReturn(taxi);
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            BookingDTO result = bookingService.takeBooking(BOOKING_ID, 453L);

            assertNotNull(result);
            verify(bookingRepository).findBookingForUpdate(anyLong());
            verify(taxiService).findById(anyLong());
            verify(taxiService).update(taxiCaptor.capture());
            verify(bookingRepository).saveAndFlush(bookingCaptor.capture());
            verify(bookingMapper).map(any(Booking.class));

            Taxi captoredTaxi = taxiCaptor.getValue();
            assertEquals(BOOKED, captoredTaxi.getStatus());

            Booking captoredBooking = bookingCaptor.getValue();
            assertEquals(CONFIRMED, captoredBooking.getStatus());
        }
    }

    @Nested
    class CompleteBooking {

        @Test
        void shouldThrowBookingNotFoundException() {
            when(bookingRepository.findBookingById(anyLong())).thenThrow(new BookingNotFoundException());

            assertThrows(BookingNotFoundException.class, () -> bookingService.completeBooking(BOOKING_ID));

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(taxiService);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void shouldThrowInvalidBookingStateExceptionAsBookingHasWrongStatus() {
            Booking booking = new Booking();
            booking.setStatus(PENDING);
            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);

            InvalidBookingStateException exception = assertThrows(InvalidBookingStateException.class, () -> bookingService.completeBooking(BOOKING_ID));
            assertEquals("exception.conflict.booking-cannot-be-completed", exception.getMessage());

            verify(bookingRepository).findBookingById(anyLong());
            verifyNoMoreInteractions(bookingRepository);
            verifyNoInteractions(bookingMapper);
        }

        @Test
        void completeBookingSuccessfully() {
            Taxi taxi = new Taxi();
            taxi.setStatus(BOOKED);
            Booking booking = new Booking();
            booking.setStatus(CONFIRMED);
            booking.setTaxi(taxi);

            when(bookingRepository.findBookingById(anyLong())).thenReturn(booking);
            when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDTO());

            BookingDTO result = bookingService.completeBooking(BOOKING_ID);

            assertNotNull(result);
            verify(bookingRepository).findBookingById(anyLong());
            verify(taxiService).update(taxiCaptor.capture());
            verify(bookingRepository).saveAndFlush(bookingCaptor.capture());
            verify(bookingMapper).map(any(Booking.class));

            Taxi captoredTaxi = taxiCaptor.getValue();
            assertEquals(AVAILABLE, captoredTaxi.getStatus());

            Booking captoredBooking = bookingCaptor.getValue();
            assertEquals(COMPLETED, captoredBooking.getStatus());
        }
    }

    private ModifyBookingDTO createBookingDto() {
        ModifyBookingDTO booking = new ModifyBookingDTO();
        booking.setPassengerId(324L);
        booking.setPickupLocation(createLocationDto(23d, 34d));
        booking.setDropOffLocation(createLocationDto(58d, 93d));
        return booking;
    }

    private ModifyLocationDTO createLocationDto(Double latitude, Double longitude) {
        ModifyLocationDTO location = new ModifyLocationDTO();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private Location createLocation(Double latitude, Double longitude) {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}