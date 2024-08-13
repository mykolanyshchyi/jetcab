package com.jetcab.controller;

import com.jetcab.common.PageableList;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.taxi.TaxiService;
import com.jetcab.service.taxi.dto.ModifyTaxiDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.model.TaxiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaxiController {

    private final TaxiService taxiService;

    @GetMapping("/api/v1/taxis")
    public PageableList<TaxiDTO> getAllTaxis(Pageable pageable) {
        return taxiService.getAllTaxis(pageable);
    }

    @GetMapping("/api/v1/taxis/{taxiId}")
    public TaxiDTO getById(@PathVariable("taxiId") Long taxiId) {
        return taxiService.getById(taxiId);
    }

    @PostMapping("/api/v1/taxis")
    public TaxiDTO createTaxi(@RequestBody @Validated ModifyTaxiDTO dto) {
        return taxiService.createTaxi(dto);
    }

    @PutMapping("/api/v1/taxis/{taxiId}/status")
    public TaxiDTO updateStatus(@PathVariable("taxiId") Long taxiId, @RequestParam(name = "status") TaxiStatus status) {
        return taxiService.updateStatus(taxiId, status);
    }

    @PutMapping("/api/v1/taxis/{taxiId}/location")
    public TaxiDTO updateLocation(@PathVariable("taxiId") Long taxiId, @RequestBody @Validated ModifyLocationDTO location) {
        return taxiService.updateLocation(taxiId, location);
    }

    @DeleteMapping("/api/v1/taxis/{taxiId}")
    public void deleteTaxi(@PathVariable("taxiId") Long taxiId) {
        taxiService.deleteTaxi(taxiId);
    }
}
