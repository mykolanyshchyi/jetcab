package com.jetcab.service.booking;

import com.jetcab.service.booking.dto.BookingDTO;
import com.jetcab.service.booking.dto.BookingStatisticsDTO;
import com.jetcab.service.booking.dto.ModifyBookingDTO;
import com.jetcab.service.booking.model.BookingStatus;

import java.time.ZonedDateTime;

public interface BookingService {

    BookingDTO createBooking(ModifyBookingDTO dto);

    BookingDTO updateBooking(Long bookingId, ModifyBookingDTO dto);

    BookingDTO updateBookingStatus(Long bookingId, BookingStatus status);

    BookingDTO cancelBooking(Long bookingId);

    BookingStatisticsDTO getBookingStatistics(ZonedDateTime from, ZonedDateTime to);

    BookingDTO takeBooking(Long bookingId, Long taxiId);

    BookingDTO completeBooking(Long bookingId);
}
