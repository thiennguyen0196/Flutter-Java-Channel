package com.example.flutter_java_channels_demo.assignment1.data.repository;

import com.example.flutter_java_channels_demo.assignment1.data.remote.ApiService;
import com.example.flutter_java_channels_demo.assignment1.data.remote.TodoDto;
import com.example.flutter_java_channels_demo.assignment1.domain.model.Todo;
import com.example.flutter_java_channels_demo.assignment1.domain.repository.TodoRepository;
import retrofit2.Response;

public class TodoRepositoryImpl implements TodoRepository {
    private final ApiService apiService;

    public TodoRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Todo getTodoById(int id) throws Exception {
        Response<TodoDto> response = apiService.getTodo(id).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("Failed to fetch todo. HTTP " + response.code());
        }
        return response.body().toDomain();
    }
}
