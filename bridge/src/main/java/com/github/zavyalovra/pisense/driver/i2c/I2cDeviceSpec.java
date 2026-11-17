package com.github.zavyalovra.pisense.driver.i2c;

import com.github.zavyalovra.pisense.common.model.SensorType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Описание I2C-устройства из свойства {@code pisense.i2c.devices}.
 * Формат записи: {@code 0x76:PRESSURE[:описание]}.
 */
record I2cDeviceSpec(int address, SensorType type, String description) {

    private static final int MIN_ADDRESS = 0x08;
    private static final int MAX_ADDRESS = 0x77;

    /**
     * Разбирает список вида {@code 0x76:PRESSURE:BMP280 pressure,0x23:LIGHT}.
     * Пустая строка означает «устройств нет».
     */
    static List<I2cDeviceSpec> parseAll(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .map(I2cDeviceSpec::parse)
                .toList();
    }

    static I2cDeviceSpec parse(String entry) {
        String[] parts = entry.split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Ожидается формат адрес:ТИП[:описание], получено: " + entry);
        }
        int address = parseAddress(parts[0]);
        SensorType type;
        try {
            type = SensorType.valueOf(parts[1].trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный тип датчика: " + parts[1], e);
        }
        String description = parts.length > 2 && !parts[2].isBlank()
                ? parts[2].trim()
                : "I2C " + type + " " + addressAsHex(address);
        return new I2cDeviceSpec(address, type, description);
    }

    /**
     * Принимает и {@code 0x76}, и {@code 118}.
     */
    static int parseAddress(String value) {
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        int address;
        try {
            address = trimmed.startsWith("0x")
                    ? Integer.parseInt(trimmed.substring(2), 16)
                    : Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный I2C адрес: " + value, e);
        }
        if (address < MIN_ADDRESS || address > MAX_ADDRESS) {
            throw new IllegalArgumentException(
                    "Адрес I2C вне диапазона 0x08..0x77: " + value);
        }
        return address;
    }

    static String addressAsHex(int address) {
        return String.format(Locale.ROOT, "0x%02X", address);
    }

    String addressAsHex() {
        return addressAsHex(address);
    }
}
