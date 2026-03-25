package com.example.flutter_java_channels_demo.assignment1.data.remote;

import com.example.flutter_java_channels_demo.assignment1.domain.model.Todo;

public class TodoDto {
    private int userId;
    private int id;
    private String title;
    private boolean completed;

    public Todo toDomain() {
        return new Todo(userId, id, title, completed);
    }
}
