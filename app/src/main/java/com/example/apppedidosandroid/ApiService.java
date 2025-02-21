package com.example.apppedidosandroid;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/buscar_app")
    Call<List<Game>> getGames(@Body Map<String, Object> appName);
}