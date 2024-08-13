package com.jetcab.service.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class BookingStatisticsDTO implements Serializable {
    private long totalBookings;
    private long inProgressBookings;
    private long completedBookings;
    private long cancelledBookings;
}
