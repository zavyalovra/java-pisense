package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Table(name = "sensor_channels", schema = "public")
@Getter
@Setter
@ToString
public class SensorChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @ToString.Exclude
    private Sensor sensor;

    @Column(name = "hardware_channel", nullable = false)
    private String hardwareChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type", nullable = false)
    private SensorType sensorType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_class_id")
    @ToString.Exclude
    private MeasurementClass measurementClass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorChannel)) return false;
        return id != null && id.equals(((SensorChannel) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
