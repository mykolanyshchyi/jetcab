package com.jetcab.service.passenger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PassengerDTO implements Serializable {
    private String name;
    private String phoneNumber;
}
