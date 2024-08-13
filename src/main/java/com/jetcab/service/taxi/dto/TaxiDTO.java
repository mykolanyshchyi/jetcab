package com.jetcab.service.taxi.dto;

import com.jetcab.service.location.dto.LocationDTO;
import com.jetcab.service.taxi.model.TaxiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiDTO implements Serializable {
    private Long id;
    private String licensePlate;
    private TaxiStatus status;
    private LocationDTO location;
}
