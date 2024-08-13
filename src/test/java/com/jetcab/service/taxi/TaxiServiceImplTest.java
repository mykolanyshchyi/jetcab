package com.jetcab.service.taxi;

import com.jetcab.common.PageableList;
import com.jetcab.persistence.TaxiRepository;
import com.jetcab.service.location.LocationService;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.location.model.Location;
import com.jetcab.service.taxi.dto.ModifyTaxiDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.exception.TaxiNotFoundException;
import com.jetcab.service.taxi.mapper.TaxiMapper;
import com.jetcab.service.taxi.model.Taxi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.jetcab.service.taxi.model.TaxiStatus.AVAILABLE;
import static com.jetcab.service.taxi.model.TaxiStatus.BOOKED;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxiServiceImplTest {

    private static final String LICENSE_PLATE = "MR543GL";
    private static final String LICENSE_PLATE_2 = "M3GL6";
    private static final long TAXI_ID = 1L;
    private static final long TAXI_ID_2 = 2L;

    @Mock
    private TaxiRepository taxiRepository;

    @Mock
    private TaxiMapper taxiMapper;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxiServiceImpl taxiService;

    @Captor
    private ArgumentCaptor<Taxi> taxiCaptor;

    @Test
    void getAllTaxis_shouldReturnEmptyPageableList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Taxi> taxiPage = new PageImpl<>(emptyList(), pageable, 0);

        when(taxiRepository.findAll(pageable)).thenReturn(taxiPage);
        when(taxiMapper.mapToPageableList(taxiPage, pageable)).thenReturn(PageableList.of(emptyList(), pageable, 0));

        PageableList<TaxiDTO> actual = taxiService.getAllTaxis(pageable);

        assertNotNull(actual);
        assertEquals(0L, actual.getTotalElements());

        verify(taxiRepository).findAll(pageable);
        verify(taxiMapper).mapToPageableList(taxiPage, pageable);
    }

    @Test
    void getAllTaxis_shouldReturnTaxisAsPageableList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Taxi> taxiList = List.of(createTaxi(TAXI_ID, LICENSE_PLATE), createTaxi(TAXI_ID_2, LICENSE_PLATE_2));
        List<TaxiDTO> expected = List.of(createTaxiDTO(TAXI_ID, LICENSE_PLATE), createTaxiDTO(TAXI_ID_2, LICENSE_PLATE_2));
        Page<Taxi> taxiPage = new PageImpl<>(taxiList, pageable, 2);

        when(taxiRepository.findAll(pageable)).thenReturn(taxiPage);
        when(taxiMapper.mapToPageableList(taxiPage, pageable)).thenReturn(PageableList.of(expected, pageable, 2));

        PageableList<TaxiDTO> result = taxiService.getAllTaxis(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(expected, result.getContent());

        verify(taxiRepository, times(1)).findAll(pageable);
        verify(taxiMapper, times(1)).mapToPageableList(taxiPage, pageable);
    }

    @Test
    void getById_shouldReturnTaxiDTO() {
        Taxi taxi = createTaxi(TAXI_ID, LICENSE_PLATE);
        TaxiDTO expected = createTaxiDTO(TAXI_ID, LICENSE_PLATE);

        when(taxiRepository.findTaxiById(TAXI_ID)).thenReturn(taxi);
        when(taxiMapper.map(taxi)).thenReturn(expected);

        TaxiDTO actual = taxiService.getById(TAXI_ID);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getLicensePlate(), actual.getLicensePlate());

        verify(taxiRepository, times(1)).findTaxiById(TAXI_ID);
        verify(taxiMapper, times(1)).map(taxi);
    }

    @Test
    void getById_shouldThrowTaxiNotFoundException() {
        when(taxiRepository.findTaxiById(anyLong())).thenThrow(new TaxiNotFoundException());
        assertThrows(TaxiNotFoundException.class, () -> taxiService.getById(TAXI_ID));
        verify(taxiRepository).findTaxiById(anyLong());
    }

    @Test
    void findById_shouldReturnTaxi() {
        Taxi taxi = createTaxi(TAXI_ID, LICENSE_PLATE);
        when(taxiRepository.findTaxiById(TAXI_ID)).thenReturn(taxi);

        Taxi result = taxiService.findById(TAXI_ID);

        assertNotNull(result);
        assertEquals(TAXI_ID, result.getId());
        assertEquals(LICENSE_PLATE, result.getLicensePlate());
        verify(taxiRepository).findTaxiById(TAXI_ID);
    }

    @Test
    void findById_shouldThrowTaxiNotFoundException() {
        when(taxiRepository.findTaxiById(TAXI_ID)).thenThrow(new TaxiNotFoundException());
        assertThrows(TaxiNotFoundException.class, () -> taxiService.findById(TAXI_ID));
        verify(taxiRepository).findTaxiById(anyLong());
    }

    @Test
    void createTaxi() {
        when(locationService.findOrCreate(any(ModifyLocationDTO.class))).thenReturn(new Location());
        when(taxiRepository.saveAndFlush(any(Taxi.class))).thenReturn(new Taxi());
        when(taxiMapper.map(any(Taxi.class))).thenReturn(createTaxiDTO(TAXI_ID, LICENSE_PLATE));

        ModifyTaxiDTO modifyTaxiDTO = new ModifyTaxiDTO();
        modifyTaxiDTO.setLocation(new ModifyLocationDTO());
        TaxiDTO result = taxiService.createTaxi(modifyTaxiDTO);

        assertNotNull(result);
        assertEquals(TAXI_ID, result.getId());
        assertEquals(LICENSE_PLATE, result.getLicensePlate());

        verify(locationService).findOrCreate(any(ModifyLocationDTO.class));
        verify(taxiRepository).saveAndFlush(any(Taxi.class));
        verify(taxiMapper).map(any(Taxi.class));
    }

    @Test
    void updateStatus_shouldThrowTaxiNotFoundException() {
        when(taxiRepository.findTaxiById(TAXI_ID)).thenThrow(new TaxiNotFoundException());
        assertThrows(TaxiNotFoundException.class, () -> taxiService.updateStatus(TAXI_ID, BOOKED));
        verify(taxiRepository).findTaxiById(anyLong());
        verifyNoMoreInteractions(taxiRepository);
    }

    @Test
    void updateStatus_shouldUpdateSuccessfully() {
        Taxi taxiBooked = createTaxi(TAXI_ID, LICENSE_PLATE);
        taxiBooked.setStatus(BOOKED);

        Taxi taxiAvailable = createTaxi(TAXI_ID, LICENSE_PLATE);
        taxiAvailable.setStatus(AVAILABLE);

        when(taxiRepository.findTaxiById(anyLong())).thenReturn(taxiBooked);
        when(taxiRepository.saveAndFlush(any(Taxi.class))).thenReturn(taxiAvailable);
        when(taxiMapper.map(taxiAvailable)).thenReturn(new TaxiDTO());

        TaxiDTO result = taxiService.updateStatus(TAXI_ID, AVAILABLE);

        assertNotNull(result);
        verify(taxiRepository).findTaxiById(anyLong());
        verify(taxiRepository).saveAndFlush(any(Taxi.class));
        verify(taxiMapper).map(taxiCaptor.capture());

        Taxi captoredTaxi = taxiCaptor.getValue();
        assertEquals(TAXI_ID, captoredTaxi.getId());
        assertEquals(LICENSE_PLATE, captoredTaxi.getLicensePlate());
        assertEquals(AVAILABLE, captoredTaxi.getStatus());
    }

    @Test
    void updateTaxi() {
        Taxi taxi = new Taxi();
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);
        taxiService.update(taxi);
        verify(taxiRepository).save(any(Taxi.class));
    }

    @Test
    void updateLocation_shouldThrowTaxiNotFoundException() {
        when(taxiRepository.findTaxiById(anyLong())).thenThrow(new TaxiNotFoundException());
        assertThrows(TaxiNotFoundException.class, () -> taxiService.updateLocation(TAXI_ID, new ModifyLocationDTO()));
        verify(taxiRepository).findTaxiById(anyLong());
        verifyNoMoreInteractions(taxiRepository);
    }

    @Test
    void updateLocation_shouldUpdateLocationSuccessfully() {
        Taxi taxi = new Taxi();
        when(taxiRepository.findTaxiById(TAXI_ID)).thenReturn(taxi);
        when(locationService.findOrCreate(any(ModifyLocationDTO.class))).thenReturn(new Location());
        when(taxiRepository.saveAndFlush(any(Taxi.class))).thenReturn(taxi);
        when(taxiMapper.map(any(Taxi.class))).thenReturn(new TaxiDTO());

        TaxiDTO result = taxiService.updateLocation(TAXI_ID, new ModifyLocationDTO());

        assertNotNull(result);
        verify(taxiRepository).findTaxiById(anyLong());
        verify(locationService).findOrCreate(any(ModifyLocationDTO.class));
        verify(taxiRepository).saveAndFlush(any(Taxi.class));
        verify(taxiMapper).map(any(Taxi.class));
    }

    @Test
    void deleteTaxi_shouldThrowTaxiNotFoundException() {
        when(taxiRepository.findTaxiById(anyLong())).thenThrow(new TaxiNotFoundException());
        assertThrows(TaxiNotFoundException.class, () -> taxiService.deleteTaxi(TAXI_ID));
        verify(taxiRepository).findTaxiById(anyLong());
        verifyNoMoreInteractions(taxiRepository);
    }

    @Test
    void deleteTaxi_shouldMarkTaxiAsDeleted() {
        Taxi taxi = new Taxi();

        when(taxiRepository.findTaxiById(anyLong())).thenReturn(taxi);
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);

        taxiService.deleteTaxi(TAXI_ID);

        verify(taxiRepository).findTaxiById(TAXI_ID);
        verify(taxiRepository).save(taxiCaptor.capture());

        Taxi deletedTaxi = taxiCaptor.getValue();
        assertNotNull(deletedTaxi);
        assertTrue(deletedTaxi.isDeleted());
    }

    @Test
    void getAvailableTaxiIDs_shouldReturnEmptyList() {
        when(taxiRepository.findAllByStatus(AVAILABLE)).thenReturn(emptyList());
        List<Long> result = taxiService.getAvailableTaxiIDs();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taxiRepository).findAllByStatus(AVAILABLE);
    }

    @Test
    void getAvailableTaxiIDs_shouldReturnListOfAvailableTaxiIds() {
        List<Long> taxiIds = List.of(TAXI_ID, TAXI_ID_2);
        when(taxiRepository.findAllByStatus(AVAILABLE)).thenReturn(taxiIds);
        List<Long> result = taxiService.getAvailableTaxiIDs();

        assertNotNull(result);
        assertEquals(taxiIds, result);
        verify(taxiRepository).findAllByStatus(AVAILABLE);
    }

    private Taxi createTaxi(Long id, String licensePlate) {
        Taxi taxi = new Taxi();
        taxi.setId(id);
        taxi.setLicensePlate(licensePlate);
        taxi.setStatus(AVAILABLE);
        return taxi;
    }

    private TaxiDTO createTaxiDTO(Long id, String licensePlate) {
        return TaxiDTO.builder()
                .id(id)
                .licensePlate(licensePlate)
                .status(AVAILABLE)
                .build();
    }
}