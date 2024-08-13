package com.jetcab.service.booking.mapper;

import com.jetcab.common.BaseModelMapper;
import com.jetcab.service.booking.dto.BookingDTO;
import com.jetcab.service.booking.model.Booking;
import com.jetcab.service.location.mapper.LocationMapper;
import com.jetcab.service.passenger.mapper.PassengerMapper;
import com.jetcab.service.passenger.model.Passenger;
import com.jetcab.service.taxi.mapper.TaxiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper extends BaseModelMapper<Booking, BookingDTO> {

    private final PassengerMapper passengerMapper;
    private final LocationMapper locationMapper;
    private final TaxiMapper taxiMapper;

    @Override
    public BookingDTO map(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .bookedAt(booking.getBookedAt())
                .status(booking.getStatus())
                .pickupLocation(locationMapper.map(booking.getPickupLocation()))
                .dropOffLocation(locationMapper.map(booking.getDropOffLocation()))
                .passenger(passengerMapper.map(booking.getPassenger()))
                .taxi(taxiMapper.map(booking.getTaxi()))
                .build();
    }
}
