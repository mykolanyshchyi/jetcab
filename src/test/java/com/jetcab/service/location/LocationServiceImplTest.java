package com.jetcab.service.location;

import com.jetcab.persistence.LocationRepository;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.location.model.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {
    private static final Long LOCATION_ID = 34L;
    private static final Double LATITUDE = 567435d;
    private static final Double LONGITUDE = 674353d;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void findOrCreate_findExistingLocation() {
        when(locationRepository.findAllByLatitudeAndLongitude(anyDouble(), anyDouble())).thenReturn(List.of(getLocation()));
        Location actual = locationService.findOrCreate(getModifyLocationDTO());
        assertNotNull(actual);
        assertEquals(LOCATION_ID, actual.getId());
        assertEquals(LATITUDE, actual.getLatitude());
        assertEquals(LONGITUDE, actual.getLongitude());

        verify(locationRepository).findAllByLatitudeAndLongitude(anyDouble(), anyDouble());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void findOrCreate_createNewLocation() {
        when(locationRepository.findAllByLatitudeAndLongitude(anyDouble(), anyDouble())).thenReturn(emptyList());
        when(locationRepository.saveAndFlush(any(Location.class))).thenReturn(getLocation());
        Location actual = locationService.findOrCreate(getModifyLocationDTO());
        assertNotNull(actual);
        assertEquals(LOCATION_ID, actual.getId());
        assertEquals(LATITUDE, actual.getLatitude());
        assertEquals(LONGITUDE, actual.getLongitude());

        verify(locationRepository).findAllByLatitudeAndLongitude(anyDouble(), anyDouble());
        verify(locationRepository).saveAndFlush(any(Location.class));
        verifyNoMoreInteractions(locationRepository);
    }

    private ModifyLocationDTO getModifyLocationDTO() {
        ModifyLocationDTO dto = new ModifyLocationDTO();
        dto.setLatitude(LATITUDE);
        dto.setLongitude(LONGITUDE);
        return dto;
    }

    private Location getLocation() {
        Location location = new Location();
        location.setId(LOCATION_ID);
        location.setLatitude(LATITUDE);
        location.setLongitude(LONGITUDE);
        return location;
    }
}