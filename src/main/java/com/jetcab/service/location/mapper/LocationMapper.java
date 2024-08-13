package com.jetcab.service.location.mapper;

import com.jetcab.common.BaseModelMapper;
import com.jetcab.service.location.dto.LocationDTO;
import com.jetcab.service.location.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper extends BaseModelMapper<Location, LocationDTO> {

    @Override
    public LocationDTO map(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
