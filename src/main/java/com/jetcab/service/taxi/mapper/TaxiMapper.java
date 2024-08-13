package com.jetcab.service.taxi.mapper;

import com.jetcab.common.BaseModelMapper;
import com.jetcab.service.location.mapper.LocationMapper;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.model.Taxi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaxiMapper extends BaseModelMapper<Taxi, TaxiDTO> {

    private final LocationMapper locationMapper;

    @Override
    public TaxiDTO map(Taxi taxi) {
        return TaxiDTO.builder()
                .id(taxi.getId())
                .licensePlate(taxi.getLicensePlate())
                .status(taxi.getStatus())
                .location(locationMapper.map(taxi.getLocation()))
                .build();
    }
}
