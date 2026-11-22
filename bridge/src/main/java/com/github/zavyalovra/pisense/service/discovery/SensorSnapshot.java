package com.github.zavyalovra.pisense.service.discovery;

import com.github.zavyalovra.pisense.model.ChannelInfo;
import com.github.zavyalovra.pisense.model.DetectedSensor;
import lombok.Getter;

import java.util.List;

@Getter
public class SensorSnapshot {

    private final DetectedSensor detected;
    private final List<ChannelInfo> channels;

    public SensorSnapshot(DetectedSensor detected, List<ChannelInfo> channels) {
        this.detected = detected;
        this.channels = List.copyOf(channels);
    }
}
