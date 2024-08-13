package com.jetcab.service.location.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LocationDTO implements Serializable {
    private Long id;
    private Double latitude;
    private Double longitude;
}
