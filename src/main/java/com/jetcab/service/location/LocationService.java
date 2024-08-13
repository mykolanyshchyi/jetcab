package com.jetcab.service.location;

import com.jetcab.service.location.dto.ModifyLocationDTO;
import com.jetcab.service.location.model.Location;

public interface LocationService {

    Location findOrCreate(ModifyLocationDTO dto);
}
