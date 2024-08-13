package com.jetcab.service.taxi;

import com.jetcab.common.PageableList;
import com.jetcab.persistence.TaxiRepository;
import com.jetcab.service.location.LocationService;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.taxi.dto.ModifyTaxiDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.mapper.TaxiMapper;
import com.jetcab.service.taxi.model.Taxi;
import com.jetcab.service.taxi.model.TaxiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jetcab.service.taxi.model.TaxiStatus.AVAILABLE;

@Service
@RequiredArgsConstructor
public class TaxiServiceImpl implements TaxiService {

    private final TaxiRepository taxiRepository;
    private final TaxiMapper taxiMapper;

    private final LocationService locationService;

    @Override
    @Transactional(readOnly = true)
    public PageableList<TaxiDTO> getAllTaxis(Pageable pageable) {
        Page<Taxi> taxiPage = taxiRepository.findAll(pageable);
        return taxiMapper.mapToPageableList(taxiPage, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public TaxiDTO getById(Long id) {
        Taxi taxi = taxiRepository.findTaxiById(id);
        return taxiMapper.map(taxi);
    }

    @Override
    @Transactional(readOnly = true)
    public Taxi findById(Long id) {
        return taxiRepository.findTaxiById(id);
    }

    @Override
    @Transactional
    public TaxiDTO createTaxi(ModifyTaxiDTO dto) {
        Taxi taxi = new Taxi();
        taxi.setLicensePlate(dto.getLicensePlate());
        taxi.setStatus(taxi.getStatus());
        taxi.setLocation(locationService.findOrCreate(dto.getLocation()));
        Taxi saved = taxiRepository.saveAndFlush(taxi);
        return taxiMapper.map(saved);
    }

    @Override
    @Transactional
    public TaxiDTO updateStatus(Long id, TaxiStatus status) {
        Taxi taxi = taxiRepository.findTaxiById(id);
        taxi.setStatus(status);
        Taxi updated = taxiRepository.saveAndFlush(taxi);
        return taxiMapper.map(updated);
    }

    @Override
    @Transactional
    public void update(Taxi taxi) {
        taxiRepository.save(taxi);
    }

    @Override
    @Transactional
    public TaxiDTO updateLocation(Long id, ModifyLocationDTO location) {
        Taxi taxi = taxiRepository.findTaxiById(id);
        taxi.setLocation(locationService.findOrCreate(location));
        Taxi updated = taxiRepository.saveAndFlush(taxi);
        return taxiMapper.map(updated);
    }

    @Override
    @Transactional
    public void deleteTaxi(Long id) {
        Taxi taxi = taxiRepository.findTaxiById(id);
        taxi.setDeleted(true);
        taxiRepository.save(taxi);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAvailableTaxiIDs() {
        return taxiRepository.findAllByStatus(AVAILABLE);
    }
}
