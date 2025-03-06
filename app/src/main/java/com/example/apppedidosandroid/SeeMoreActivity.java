package com.example.apppedidosandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.RectangularItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeeMoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        String gameName = getIntent().getStringExtra("gameNames");
        if (gameName != null) {
            fetchGameDetails(gameName);
        }
    }

    private void fetchGameDetails(String game) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.34.124.156:5000")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Map<String, Object> request = Map.of("app_name", game, "n_hits", 48);
        Call<List<Game>> callGameDetails = apiService.getGames(request);

        callGameDetails.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                List<Game> games = response.body();
                // Filter out games without images
                List<Game> filteredGames = new ArrayList<>();
                for (Game game : games) {
                    if (game.getImagenes() != null && !game.getImagenes().isEmpty() && !game.getImagenes().get(0).equals("default_image_url")) {
                        filteredGames.add(game);
                    }
                }
                recyclerView.setAdapter(new RectangularItemAdapter(filteredGames, SeeMoreActivity.this));
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(SeeMoreActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void response(int responseCode, String responseMessage) {
        Log.e("API_ERROR", "Response code: " + responseCode);
        Log.e("API_ERROR", "Response message: " + responseMessage);
        Toast.makeText(SeeMoreActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
    }
}