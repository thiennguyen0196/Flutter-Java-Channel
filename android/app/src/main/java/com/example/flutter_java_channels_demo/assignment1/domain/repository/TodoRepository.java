package com.example.flutter_java_channels_demo.assignment1.domain.repository;

import com.example.flutter_java_channels_demo.assignment1.domain.model.Todo;

public interface TodoRepository {
    Todo getTodoById(int id) throws Exception;
}
