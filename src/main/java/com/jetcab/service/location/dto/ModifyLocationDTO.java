package com.jetcab.service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyLocationDTO implements Serializable {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
