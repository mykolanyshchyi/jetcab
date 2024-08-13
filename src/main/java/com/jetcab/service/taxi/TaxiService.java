package com.jetcab.service.taxi;

import com.jetcab.common.PageableList;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.taxi.dto.ModifyTaxiDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.model.Taxi;
import com.jetcab.service.taxi.model.TaxiStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaxiService {

    PageableList<TaxiDTO> getAllTaxis(Pageable pageable);

    TaxiDTO getById(Long id);

    Taxi findById(Long id);

    TaxiDTO createTaxi(ModifyTaxiDTO dto);

    TaxiDTO updateStatus(Long id, TaxiStatus status);

    void update(Taxi taxi);

    TaxiDTO updateLocation(Long id, ModifyLocationDTO location);

    void deleteTaxi(Long id);

    List<Long> getAvailableTaxiIDs();
}
