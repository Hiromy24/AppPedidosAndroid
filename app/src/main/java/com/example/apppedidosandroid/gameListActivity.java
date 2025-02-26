package com.example.apppedidosandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.example.apppedidosandroid.adapters.RectangularItemAdapter;
import com.example.apppedidosandroid.adapters.SquareItemAdapter;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class gameListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchRandomGames();
    }
    private void fetchRandomGames() {
        Retrofit retrofit;
        ApiService apiService;
        Map<String, Object> request;
        Map<String, Object> request1;
        Call<List<Game>> callRandomGames;
        Call<List<Game>> callStrategyGames;

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.34.121.44:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        // Llamada a la API (aquí debes enviar el nombre de la app o dejarlo vacío para obtener juegos aleatorios)
        request = Map.of("app_name", "");
        request1 = Map.of("category", "Strategy", "n_hits", 12);
        callRandomGames = apiService.getGames(request);
        callStrategyGames = apiService.getGames(request1);

        //region Callbacks
        callRandomGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerView.setAdapter(new SquareItemAdapter(response.body()));
                new PagerSnapHelper().attachToRecyclerView(recyclerView);
            }
            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al obtener los juegos", Toast.LENGTH_SHORT).show();
            }
        });

        callStrategyGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerView1.setAdapter(new RectangularItemAdapter(response.body()));
                new PagerSnapHelper().attachToRecyclerView(recyclerView1);
            }
            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al obtener los juegos", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion
    }

}