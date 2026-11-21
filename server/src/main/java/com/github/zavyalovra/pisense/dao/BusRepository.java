package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBridgeIdAndBusName(Long bridgeId, String busName);
}
