package com.example.flutter_java_channels_demo.di;

public class ChannelConfig {
    private final String methodChannelName;
    private final String eventChannelName;

    public ChannelConfig(String methodChannelName, String eventChannelName) {
        this.methodChannelName = methodChannelName;
        this.eventChannelName = eventChannelName;
    }

    public String getMethodChannelName() {
        return methodChannelName;
    }

    public String getEventChannelName() {
        return eventChannelName;
    }
}
