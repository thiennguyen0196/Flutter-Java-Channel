package com.example.flutter_java_channels_demo.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public final class ChannelModule {
    private ChannelModule() {
    }

    @Provides
    @Singleton
    static ChannelConfig provideChannelConfig() {
        return new ChannelConfig(
                "com.example.flutter_java_channels/plugin_method",
                "com.example.flutter_java_channels/plugin_event"
        );
    }
}
