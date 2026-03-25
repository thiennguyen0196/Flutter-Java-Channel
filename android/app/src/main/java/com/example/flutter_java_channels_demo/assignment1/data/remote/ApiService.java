package com.example.flutter_java_channels_demo.assignment1.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("todos/{id}")
    Call<TodoDto> getTodo(@Path("id") int id);
}
