package com.github.zavyalovra.pisense.sensor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Активна только при SPRING_PROFILES_ACTIVE=mock.
 * Реальные драйверы I2C/SPI/1-Wire должны быть помечены @Profile("pi").
 */
@Configuration
@Profile("mock")
public class MockSensorsConfig {
    @Bean
    SensorReader mockTemperature() {
        return new MockSensorReader(SensorType.TEMPERATURE, 22.0, 3.0);
    }

    @Bean
    SensorReader mockPressure() {
        return new MockSensorReader(SensorType.PRESSURE, 1013.0, 5.0);
    }

    @Bean
    SensorReader mockHumidity() {
        return new MockSensorReader(SensorType.HUMIDITY, 45.0, 10.0);
    }

    @Bean
    SensorReader mockLight() {
        return new MockSensorReader(SensorType.LIGHT, 300.0, 200.0);
    }
}