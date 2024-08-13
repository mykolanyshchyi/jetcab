package com.jetcab.service.booking.dto;

import com.jetcab.service.location.dto.ModifyLocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModifyBookingDTO implements Serializable {

    @NotNull
    private Long passengerId;

    @NotNull
    @Valid
    private ModifyLocationDTO pickupLocation;

    @NotNull
    @Valid
    private ModifyLocationDTO dropOffLocation;
}
