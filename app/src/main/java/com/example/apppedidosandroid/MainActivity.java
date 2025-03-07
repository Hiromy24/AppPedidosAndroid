package com.example.apppedidosandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.PopularItemAdapter;
import com.example.apppedidosandroid.adapters.RectangularItemAdapter;
import com.example.apppedidosandroid.adapters.SquareItemAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> loginLauncher;
    RecyclerView recyclerView, recyclerView1, recyclerView3;
    MaterialToolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
    });
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // Aplica el tema
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        showProfileSheet();
                    }
                });

        linkComponents();
        setSupportActionBar(topAppBar);
        recyclerViewManager();
        fetchRandomGames(); // Llamar al método que obtiene los juegos

    }
    void linkComponents(){
        recyclerView = findViewById(R.id.squareItemRecyclerView);
        recyclerView1 = findViewById(R.id.rectangularItemRecyclerView);
        recyclerView3 = findViewById(R.id.popularItemRecyclerView);
        topAppBar = findViewById(R.id.topAppBar);
    }
    //region RecyclerView
    void recyclerViewManager(){
        setRecyclerLayout();
        setRecyclerItemDecoration();
    }
    void setRecyclerLayout(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    void setRecyclerItemDecoration(){
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView1.addItemDecoration(new DividerItemDecoration(recyclerView1.getContext(),DividerItemDecoration.HORIZONTAL));
    }
    //endregion
    //region ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_cart) {
            // Acción para el botón de carrito
            return true;
        } else if (id == R.id.action_profile) {
            showProfileSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    public void moreGames(View view){
        Intent intent = new Intent(this, ProfileSheet.class);
        intent.putExtra("value", view.getTag().toString());
        startActivity(intent);
    }
    public void showProfileSheet() {
        SharedPreferences preferences;
        preferences = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        if (preferences.getString("email", null) != null) {
            ProfileSheet profileSheet = new ProfileSheet();
            profileSheet.show(getSupportFragmentManager(), "ProfileSheet");
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            loginLauncher.launch(intent);
        }
    }
    private void fetchRandomGames() {
        Retrofit retrofit;
        ApiService apiService;
        Map<String, Object> request;
        Map<String, Object> request1;
        Map<String, Object> request2;
        Call<List<Game>> callRandomGames;
        Call<List<Game>> callStrategyGames;
        Call<List<Game>> callPopularGames;

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.34.124.156:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        // Llamada a la API (aquí debes enviar el nombre de la app o dejarlo vacío para obtener juegos aleatorios)
        request = Map.of("app_name", "");
        request1 = Map.of("category", "Strategy", "n_hits", 12);
        request2 = Map.of("app_names", List.of("Among Us", "Bullet Echo", "Roblox",
                "JCC Pokemon Pocket", "Wild Rift"));
        callRandomGames = apiService.getGames(request);
        callStrategyGames = apiService.getGames(request1);
        callPopularGames = apiService.getGameInfo(request2);

        //region Callbacks
        callRandomGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerView.setAdapter(new SquareItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerView);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });

        callStrategyGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerView1.setAdapter(new RectangularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerView1);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });

        callPopularGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerView3.setAdapter(new PopularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerView3);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion
    }
    void response(int responseCode, String responseMessage) {
        Log.e("API_ERROR", "Response code: " + responseCode);
        Log.e("API_ERROR", "Response message: " + responseMessage);
        Toast.makeText(MainActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
    }

}
