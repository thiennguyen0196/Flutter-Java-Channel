package com.example.flutter_java_channels_demo.assignment1.domain.usecase;

import com.example.flutter_java_channels_demo.assignment1.domain.model.Todo;
import com.example.flutter_java_channels_demo.assignment1.domain.repository.TodoRepository;

public class GetTodoUseCase {
    private final TodoRepository repository;

    public GetTodoUseCase(TodoRepository repository) {
        this.repository = repository;
    }

    public Todo execute(int id) throws Exception {
        return repository.getTodoById(id);
    }
}
