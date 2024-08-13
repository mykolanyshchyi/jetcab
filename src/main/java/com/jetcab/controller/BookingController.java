package com.jetcab.controller;

import com.jetcab.service.booking.BookingService;
import com.jetcab.service.booking.dto.BookingDTO;
import com.jetcab.service.booking.dto.BookingStatisticsDTO;
import com.jetcab.service.booking.dto.ModifyBookingDTO;
import com.jetcab.service.notification.NotificationService;
import com.jetcab.service.taxi.TaxiService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final TaxiService taxiService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    @PostMapping("/api/v1/bookings")
    public BookingDTO createBooking(@RequestBody @Validated ModifyBookingDTO dto) {
        BookingDTO booking = bookingService.createBooking(dto);
        notificationService.publishBookingToAvailableTaxis(booking, taxiService.getAvailableTaxiIDs());
        return booking;
    }

    @PutMapping("/api/v1/bookings/{bookingId}")
    public BookingDTO updateBooking(@PathVariable("bookingId") Long bookingId, @RequestBody @Validated ModifyBookingDTO dto) {
        BookingDTO booking = bookingService.updateBooking(bookingId, dto);
        notificationService.publishBookingToAvailableTaxis(booking, taxiService.getAvailableTaxiIDs());
        return booking;
    }

    @DeleteMapping("/api/v1/bookings/{bookingId}")
    public BookingDTO cancelBooking(@PathVariable("bookingId") Long bookingId) {
        BookingDTO booking = bookingService.cancelBooking(bookingId);
        notificationService.publishBookingToAvailableTaxis(booking, taxiService.getAvailableTaxiIDs());
        return booking;
    }

    @PutMapping("/api/v1/bookings/{bookingId}/take-booking/{taxiId}")
    public BookingDTO takeBooking(@PathVariable Long bookingId, @PathVariable Long taxiId) {
        BookingDTO booking = bookingService.takeBooking(bookingId, taxiId);
        notificationService.publishBookingToAvailableTaxis(booking, taxiService.getAvailableTaxiIDs());
        return booking;
    }

    @PutMapping("/api/v1/bookings/{bookingId}/complete-booking")
    public BookingDTO completeBooking(@PathVariable Long bookingId) {
       return bookingService.completeBooking(bookingId);
    }

    @GetMapping("/api/v1/bookings/statistics")
    public BookingStatisticsDTO getBookingStatistics(@RequestParam(name = "from") ZonedDateTime from, @RequestParam(name = "to") ZonedDateTime to) {
        return bookingService.getBookingStatistics(from, to);
    }
}
