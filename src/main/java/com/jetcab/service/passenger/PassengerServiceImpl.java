package com.jetcab.service.passenger;

import com.jetcab.persistence.PassengerRepository;
import com.jetcab.service.passenger.model.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    @Override
    @Transactional(readOnly = true)
    public Passenger getById(Long passengerId) {
        return passengerRepository.findPassengerById(passengerId);
    }
}
