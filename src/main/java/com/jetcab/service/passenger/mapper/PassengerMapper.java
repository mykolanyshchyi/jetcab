package com.jetcab.service.passenger.mapper;

import com.jetcab.common.BaseModelMapper;
import com.jetcab.service.passenger.dto.PassengerDTO;
import com.jetcab.service.passenger.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper extends BaseModelMapper<Passenger, PassengerDTO> {

    @Override
    public PassengerDTO map(Passenger passenger) {
        return new PassengerDTO(passenger.getName(), passenger.getPhoneNumber());
    }
}
