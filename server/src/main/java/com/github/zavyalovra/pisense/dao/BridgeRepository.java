package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Bridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BridgeRepository extends JpaRepository<Bridge, Long> {

    @Query("""
            SELECT DISTINCT b
            FROM Bridge b
            LEFT JOIN FETCH b.buses bus
            LEFT JOIN FETCH bus.sensors s
            LEFT JOIN FETCH s.sensorChannels
            WHERE b.name = :bridgeName
            """)
    Optional<Bridge> findByNameWithTree(@Param("bridgeName") String bridgeName);

    Optional<Bridge> findBridgeByName(@Param("bridgeName") String bridgeName);
}
