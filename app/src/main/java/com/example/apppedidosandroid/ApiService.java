package com.example.apppedidosandroid;

import com.example.apppedidosandroid.model.Game;

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

    @Headers("Content-Type: application/json")
    @POST("/info_app")
    Call<List<Game>> getGameInfo(@Body Map<String, Object> gameInfo);
}