package com.github.zavyalovra.pisense.driver.onewire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Ds18b20DriverTest {

    @TempDir
    Path tempDir;

    private Path w1Slave(String content) throws IOException {
        Path file = tempDir.resolve("w1_slave");
        Files.writeString(file, content);
        return file;
    }

    @Test
    void parsesPositiveTemperature() throws IOException {
        Path file = w1Slave("""
                72 01 4b 46 7f ff 0e 10 57 : crc=57 YES
                72 01 4b 46 7f ff 0e 10 57 t=23125
                """);

        assertEquals(23.125, new Ds18b20Driver(file).read(), 1e-9);
    }

    @Test
    void parsesNegativeTemperature() throws IOException {
        Path file = w1Slave("""
                72 01 4b 46 7f ff 0e 10 57 : crc=57 YES
                72 01 4b 46 7f ff 0e 10 57 t=-1250
                """);

        assertEquals(-1.25, new Ds18b20Driver(file).read(), 1e-9);
    }

    @Test
    void failsOnCrcError() throws IOException {
        Path file = w1Slave("""
                72 01 4b 46 7f ff 0e 10 57 : crc=ff NO
                72 01 4b 46 7f ff 0e 10 57 t=23125
                """);

        assertThrows(UncheckedIOException.class, () -> new Ds18b20Driver(file).read());
    }

    @Test
    void failsWhenFileIsMissing() {
        Path missing = tempDir.resolve("missing/w1_slave");

        assertThrows(UncheckedIOException.class, () -> new Ds18b20Driver(missing).read());
    }
}
