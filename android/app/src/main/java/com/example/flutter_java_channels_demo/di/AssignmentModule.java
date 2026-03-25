package com.example.flutter_java_channels_demo.di;

import com.example.flutter_java_channels_demo.assignment1.data.remote.ApiService;
import com.example.flutter_java_channels_demo.assignment1.data.repository.TodoRepositoryImpl;
import com.example.flutter_java_channels_demo.assignment1.domain.repository.TodoRepository;
import com.example.flutter_java_channels_demo.assignment1.domain.usecase.GetTodoUseCase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public final class AssignmentModule {
    private AssignmentModule() {
    }

    @Provides
    @Singleton
    static TodoRepository provideTodoRepository(ApiService apiService) {
        return new TodoRepositoryImpl(apiService);
    }

    @Provides
    @Singleton
    static GetTodoUseCase provideGetTodoUseCase(TodoRepository todoRepository) {
        return new GetTodoUseCase(todoRepository);
    }
}
