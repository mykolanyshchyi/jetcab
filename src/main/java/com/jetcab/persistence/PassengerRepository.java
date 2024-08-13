package com.jetcab.persistence;

import com.jetcab.service.passenger.exception.PassengerNotFoundException;
import com.jetcab.service.passenger.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    default Passenger findPassengerById(Long passengerId) {
        return findById(passengerId).orElseThrow(PassengerNotFoundException::new);
    }
}
