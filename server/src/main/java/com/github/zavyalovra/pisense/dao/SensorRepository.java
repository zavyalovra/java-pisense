package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
