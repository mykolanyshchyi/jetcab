package com.jetcab.service.notification;

import com.jetcab.service.booking.dto.BookingDTO;

import java.util.List;

public interface NotificationService {

    void publishBookingToAvailableTaxis(BookingDTO booking, List<Long> availableTaxisIDs);

    void notifyTaxi(BookingDTO booking, Long taxiId);
}
