package com.jetcab.service.passenger;

import com.jetcab.persistence.PassengerRepository;
import com.jetcab.service.passenger.exception.PassengerNotFoundException;
import com.jetcab.service.passenger.model.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    private static final long PASSENGER_ID = 345L;
    private static final String PASSENGER_NAME = "John Smith";

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void getById_Success() {
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID);
        passenger.setName(PASSENGER_NAME);
        when(passengerRepository.findPassengerById(anyLong())).thenReturn(passenger);
        Passenger actual = passengerService.getById(PASSENGER_ID);
        assertNotNull(actual);
        assertEquals(PASSENGER_ID, actual.getId());
        assertEquals(PASSENGER_NAME, actual.getName());
    }

    @Test
    void getById_PassengerNotFound() {
        when(passengerRepository.findPassengerById(anyLong())).thenThrow(new PassengerNotFoundException());
        assertThrows(PassengerNotFoundException.class, () -> passengerService.getById(PASSENGER_ID));
    }
}