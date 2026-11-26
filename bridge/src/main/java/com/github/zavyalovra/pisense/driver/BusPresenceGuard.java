package com.github.zavyalovra.pisense.driver;

import com.github.zavyalovra.pisense.model.SensorBus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("pi")
@RequiredArgsConstructor
public class BusPresenceGuard {

    private final List<SensorBus> buses;

    @PostConstruct
    void requireAtLeastOneBus() {
        if (buses.isEmpty()) {
            throw new IllegalStateException(
                    "Не найдено ни одной шины: для профиля 'pi' нужны драйверы Pi4J");
        }
    }
}