package com.jetcab.service.location;

import com.jetcab.persistence.LocationRepository;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.location.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public Location findOrCreate(ModifyLocationDTO dto) {
        List<Location> locations = locationRepository.findAllByLatitudeAndLongitude(dto.getLatitude(), dto.getLongitude());
        if (isEmpty(locations)) {
            Location location = new Location();
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            return locationRepository.saveAndFlush(location);
        } else {
            return locations.get(0);
        }
    }
}
