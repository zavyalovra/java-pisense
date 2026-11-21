package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "measurement_classes", schema = "public")
@Getter
@Setter
@ToString
public class MeasurementClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_code", nullable = false)
    private SensorType typeCode;

    @Column(name = "unit_of_measurement", nullable = false)
    private String unitOfMeasurement;

    @Column(nullable = false)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementClass)) return false;
        return id != null && id.equals(((MeasurementClass) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
