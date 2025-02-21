package com.example.apppedidosandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.RectangularItemAdapter;
import com.example.apppedidosandroid.adapters.SquareItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv, rv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.squareItemRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

        rv2 = findViewById(R.id.rectangularItemRecyclerView);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv2.setLayoutManager(layoutManager2);

        fetchRandomGames(); // Llamar al método que obtiene los juegos
    }

    private void fetchRandomGames() {
        // Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.18.200.9:5000") // Cambia esto a la URL de tu API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Llamada a la API (aquí debes enviar el nombre de la app o dejarlo vacío para obtener juegos aleatorios)
        Map<String, Object> request = Map.of("app_name", "");
        Map<String, Object> request1 = Map.of("category", "Strategy", "n_hits", 12);
        Call<List<Game>> randomGames = apiService.getGames(request);
        Call<List<Game>> strategyGames = apiService.getGames(request1);

        // Ejecutar la llamada asíncrona
        randomGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Game> games = response.body();
                    Log.d("API_RESPONSE", "Games: " + games);
                    SquareItemAdapter adapter = new SquareItemAdapter(games);
                    rv.setAdapter(adapter);
                    PagerSnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(rv);
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    Log.e("API_ERROR", "Response message: " + response.message());
                    Toast.makeText(MainActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al obtener los juegos", Toast.LENGTH_SHORT).show();
            }
        });

       strategyGames.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Game> games = response.body();
                    Log.d("API_RESPONSE", "Games: " + games);
                    RectangularItemAdapter adapter2 = new RectangularItemAdapter(games);
                    rv2.setAdapter(adapter2);
                    PagerSnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(rv2);
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    Log.e("API_ERROR", "Response message: " + response.message());
                    Toast.makeText(MainActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error al obtener los juegos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
