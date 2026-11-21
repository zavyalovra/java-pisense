package com.github.zavyalovra.pisense.model;

public class SensorReadException extends Exception {
    public SensorReadException(String message) {
        super(message);
    }

    public SensorReadException(String message, Throwable cause) {
        super(message, cause);
    }
}