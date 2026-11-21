package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByBusIdAndAddress(Long busId, String address);

    @Query("select s " +
            "from Sensor s " +
            "join fetch s.bus b " +
            "where b.bridge.id in :bridgeIds")
    List<Sensor> findAllByBridgeIds(@Param("bridgeIds") Collection<Long> bridgeIds);
}
