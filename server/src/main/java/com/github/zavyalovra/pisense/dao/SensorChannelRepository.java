package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.SensorChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SensorChannelRepository extends JpaRepository<SensorChannel, Long> {

    Optional<SensorChannel> findBySensorIdAndHardwareChannel(Long sensorId, String hardwareChannel);

    List<SensorChannel> findAllBySensorIdIn(Collection<Long> sensorIds);
}
