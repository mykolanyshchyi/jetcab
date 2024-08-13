package com.jetcab.service.booking.dto;

import com.jetcab.service.booking.model.BookingStatus;
import com.jetcab.service.location.dto.LocationDTO;
import com.jetcab.service.passenger.dto.PassengerDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO implements Serializable {
    private Long id;
    private PassengerDTO passenger;
    private LocationDTO pickupLocation;
    private LocationDTO dropOffLocation;
    private BookingStatus status;
    private ZonedDateTime bookedAt;
    private TaxiDTO taxi;
}
