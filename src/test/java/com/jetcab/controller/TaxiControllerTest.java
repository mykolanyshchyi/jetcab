package com.jetcab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetcab.common.PageableList;
import com.jetcab.configuration.TestConfiguration;
import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.taxi.TaxiService;
import com.jetcab.service.taxi.dto.ModifyTaxiDTO;
import com.jetcab.service.taxi.dto.TaxiDTO;
import com.jetcab.service.taxi.model.TaxiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.jetcab.service.taxi.model.TaxiStatus.AVAILABLE;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaxiController.class)
@Import(TestConfiguration.class)
class TaxiControllerTest {

    private static final String LICENSE_PLATE = "ML345GH";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaxiService taxiService;

    private TaxiDTO taxiDTO;
    private ModifyTaxiDTO modifyTaxiDTO;

    @BeforeEach
    void setUp() {
        taxiDTO = TaxiDTO.builder()
                .id(1L)
                .licensePlate(LICENSE_PLATE)
                .status(AVAILABLE)
                .build();

        modifyTaxiDTO = new ModifyTaxiDTO();
        modifyTaxiDTO.setStatus(AVAILABLE);
        modifyTaxiDTO.setLicensePlate(LICENSE_PLATE);
        modifyTaxiDTO.setLocation(new ModifyLocationDTO(40.7128d, -74.0060));
    }

    @Test
    void testGetAllTaxis() throws Exception {
        when(taxiService.getAllTaxis(any(PageRequest.class)))
                .thenReturn(PageableList.of(singletonList(taxiDTO), Pageable.ofSize(10), 1));

        mockMvc.perform(get("/api/v1/taxis")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1L))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].status").value(AVAILABLE.toString()));
    }

    @Test
    void testGetById() throws Exception {
        when(taxiService.getById(1L)).thenReturn(taxiDTO);

        mockMvc.perform(get("/api/v1/taxis/{taxiId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.status").value(AVAILABLE.toString()));
    }

    @Test
    void createTaxi_success() throws Exception {
        when(taxiService.createTaxi(any(ModifyTaxiDTO.class))).thenReturn(taxiDTO);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.status").value(AVAILABLE.toString()));

        verify(taxiService).createTaxi(any(ModifyTaxiDTO.class));
    }

    @Test
    void createTaxi_missingRequiredFieldLicensePlate() throws Exception {
        modifyTaxiDTO.setLicensePlate(null);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void createTaxi_missingRequiredFieldStatus() throws Exception {
        modifyTaxiDTO.setStatus(null);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void createTaxi_missingRequiredFieldLocation() throws Exception {
        modifyTaxiDTO.setLocation(null);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void createTaxi_missingRequiredFieldLatitude() throws Exception {
        modifyTaxiDTO.getLocation().setLatitude(null);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void createTaxi_missingRequiredFieldLongitude() throws Exception {
        modifyTaxiDTO.getLocation().setLongitude(null);

        mockMvc.perform(post("/api/v1/taxis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializeToString(modifyTaxiDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void updateStatus_success() throws Exception {
        when(taxiService.updateStatus(eq(1L), eq(AVAILABLE))).thenReturn(taxiDTO);

        mockMvc.perform(put("/api/v1/taxis/{taxiId}/status", 1L)
                        .param("status", AVAILABLE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.status").value(AVAILABLE.toString()));

        verify(taxiService).updateStatus(anyLong(), any(TaxiStatus.class));
    }

    @Test
    void updateStatus_missingRequiredParamStatus() throws Exception {
        when(taxiService.updateStatus(eq(1L), eq(AVAILABLE))).thenReturn(taxiDTO);

        mockMvc.perform(put("/api/v1/taxis/{taxiId}/status", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void updateLocation_success() throws Exception {
        when(taxiService.updateLocation(eq(1L), any(ModifyLocationDTO.class))).thenReturn(taxiDTO);

        mockMvc.perform(put("/api/v1/taxis/{taxiId}/location", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latitude\": 40.7128, \"longitude\": -74.0060}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.status").value(AVAILABLE.toString()));
    }

    @Test
    void updateLocation_latitudeIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/taxis/{taxiId}/location", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latitude\": 40.7128}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void updateLocation_longitudeIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/taxis/{taxiId}/location", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longitude\": -74.0060}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taxiService);
    }

    @Test
    void deleteTaxi() throws Exception {
        doNothing().when(taxiService).deleteTaxi(1L);

        mockMvc.perform(delete("/api/v1/taxis/{taxiId}", 1L))
                .andExpect(status().isOk());

        verify(taxiService).deleteTaxi(1L);
    }

    private String serializeToString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}