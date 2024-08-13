package com.jetcab.persistence;

import com.jetcab.service.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findAllByLatitudeAndLongitude(Double latitude, Double longitude);
}
