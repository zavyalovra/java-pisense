package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Measure;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementsRepository extends JpaRepository<Measure, Long> {

    @EntityGraph(attributePaths = {"sensor"})
    @Query("select m from Measure m " +
            "where m.sensor.available = true")
    List<Measure> findMeasurementsLast();
}
