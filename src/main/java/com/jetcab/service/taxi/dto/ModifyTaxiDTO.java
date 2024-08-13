package com.jetcab.service.taxi.dto;

import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.taxi.model.TaxiStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModifyTaxiDTO implements Serializable {

    @NotBlank
    private String licensePlate;

    @NotNull
    private TaxiStatus status;

    @NotNull
    @Valid
    private ModifyLocationDTO location;
}
