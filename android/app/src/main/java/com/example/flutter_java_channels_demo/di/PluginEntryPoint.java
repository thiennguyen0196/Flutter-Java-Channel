package com.example.flutter_java_channels_demo.di;

import com.example.flutter_java_channels_demo.assignment1.domain.usecase.GetTodoUseCase;
import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface PluginEntryPoint {
    GetTodoUseCase getTodoUseCase();
    ChannelConfig getChannelConfig();
}
