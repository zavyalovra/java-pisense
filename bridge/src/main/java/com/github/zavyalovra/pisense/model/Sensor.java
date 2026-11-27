package com.github.zavyalovra.pisense.model;

import java.util.List;

/**
 * Порт одного датчика: как в README — только чтение и тип.
 */
public interface Sensor extends AutoCloseable {

    List<ChannelInfo> channels();

    List<Reading> read() throws SensorReadException;

    @Override
    default void close() {
    }
}
