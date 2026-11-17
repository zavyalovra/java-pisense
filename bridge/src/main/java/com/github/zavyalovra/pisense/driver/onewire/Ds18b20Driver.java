package com.github.zavyalovra.pisense.driver.onewire;

import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@AllArgsConstructor
class Ds18b20Driver implements SensorReader {

    /** Значение после включения питания: измерение ещё не выполнено. */
    private static final int POWER_ON_RESET_VALUE = 85000;

    private final Path w1Slave;

    @Override
    public SensorType getType() {
        return SensorType.TEMPERATURE;
    }

    /**
     * Читает файл {@code w1_slave}, который ядро обновляет для каждого датчика
     * на 1-Wire. Формат:
     * <pre>
     * 72 01 4b 46 7f ff 0e 10 57 : crc=57 YES
     * 72 01 4b 46 7f ff 0e 10 57 t=23125
     * </pre>
     * Значение {@code t=} — температура в тысячных долях градуса.
     */
    @Override
    public double read() {
        try {
            List<String> lines = Files.readAllLines(w1Slave, StandardCharsets.US_ASCII);
            if (lines.size() < 2 || !lines.get(0).trim().endsWith("YES")) {
                throw new UncheckedIOException(
                        "Ошибка CRC при чтении " + w1Slave,
                        new IOException("crc check failed"));
            }

            int index = lines.get(1).indexOf("t=");
            if (index < 0) {
                throw new UncheckedIOException(
                        "В файле нет поля t=: " + w1Slave,
                        new IOException("t= not found"));
            }

            int millis = parseMillis(lines.get(1).substring(index + 2));
            if (millis == POWER_ON_RESET_VALUE) {
                throw new IllegalStateException(
                        "DS18B20 вернул 85 °C: измерение ещё не выполнено");
            }
            return millis / 1000.0;
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать " + w1Slave, e);
        }
    }

    private int parseMillis(String rawValue) {
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            throw new UncheckedIOException(
                    "Некорректное значение t= в " + w1Slave + ": " + rawValue.trim(),
                    new IOException(e));
        }
    }
}
