package com.jetcab.persistence;

import com.jetcab.service.taxi.exception.TaxiNotFoundException;
import com.jetcab.service.taxi.model.Taxi;
import com.jetcab.service.taxi.model.TaxiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxiRepository extends JpaRepository<Taxi, Long> {

    default Taxi findTaxiById(Long id) {
        return findById(id).orElseThrow(TaxiNotFoundException::new);
    }

    List<Long> findAllByStatus(TaxiStatus status);
}
