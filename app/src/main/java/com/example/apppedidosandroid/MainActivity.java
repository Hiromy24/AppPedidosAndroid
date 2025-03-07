package com.example.apppedidosandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> loginLauncher;
    RecyclerView recyclerViewSuggestions, recyclerViewStrategy, recyclerViewPopular, recyclerViewPaid, recyclerViewAction,
            recyclerViewMulti, recyclerViewOffline;
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
        recyclerViewSuggestions = findViewById(R.id.squareItemRecyclerView);
        recyclerViewStrategy = findViewById(R.id.rectangularStrategyItemRecyclerView);
        recyclerViewPopular = findViewById(R.id.popularItemRecyclerView);
        recyclerViewAction = findViewById(R.id.rectangularActionItemRecyclerView);
        recyclerViewMulti = findViewById(R.id.rectangularItemRecyclerView);
        recyclerViewOffline = findViewById(R.id.rectangularOfflineItemRecyclerView);
        topAppBar = findViewById(R.id.topAppBar1);
    }
    //region RecyclerView
    void recyclerViewManager(){
        setRecyclerLayout();
        setRecyclerItemDecoration();
    }
    void setRecyclerLayout(){
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewStrategy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMulti.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOffline.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    void setRecyclerItemDecoration(){
        recyclerViewSuggestions.addItemDecoration(new DividerItemDecoration(recyclerViewSuggestions.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewStrategy.addItemDecoration(new DividerItemDecoration(recyclerViewStrategy.getContext(),DividerItemDecoration.HORIZONTAL));
        recyclerViewPopular.addItemDecoration(new DividerItemDecoration(recyclerViewPopular.getContext(),DividerItemDecoration.HORIZONTAL));
        recyclerViewAction.addItemDecoration(new DividerItemDecoration(recyclerViewAction.getContext(),DividerItemDecoration.HORIZONTAL));
        recyclerViewMulti.addItemDecoration(new DividerItemDecoration(recyclerViewMulti.getContext(),DividerItemDecoration.HORIZONTAL));
        recyclerViewOffline.addItemDecoration(new DividerItemDecoration(recyclerViewOffline.getContext(),DividerItemDecoration.HORIZONTAL));
    }
    //endregion
    //region ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("gameNames", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_profile) {
            showProfileSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
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
        Map<String, Object> request3;
        Map<String, Object> request4;
        Map<String, Object> request5;
        Map<String, Object> request6;
        Call<List<Game>> callRandomGames;
        Call<List<Game>> callStrategyGames;
        Call<List<Game>> callPopularGames;
        Call<List<Game>> callActionGames;
        Call<List<Game>> callMultiplayerGames;
        Call<List<Game>> callOfflineGames;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.34.126.6:5000")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        // Llamada a la API (aquí debes enviar el nombre de la app o dejarlo vacío para obtener juegos aleatorios)
        request = Map.of("app_name", "", "free", true);
        request1 = Map.of("category", "Strategy", "n_hits", 12, "free", true);
        request2 = Map.of("app_names", List.of("Among Us", "Bullet Echo", "Roblox",
                "JCC Pokemon Pocket", "Wild Rift"), "free", true);
        request4 = Map.of("category", "Action", "n_hits", 12, "free", true);
        request5 = Map.of("category", "Multiplayer", "n_hits", 12, "free", true);
        request6 = Map.of("category", "Offline", "n_hits", 12, "free", true);

        callRandomGames = apiService.getGames(request);
        callStrategyGames = apiService.getGames(request1);
        callPopularGames = apiService.getGameInfo(request2);
        callActionGames = apiService.getGames(request4);
        callMultiplayerGames = apiService.getGames(request5);
        callOfflineGames = apiService.getGames(request6);

        //region Callbacks
        callRandomGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewSuggestions.setAdapter(new SquareItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewSuggestions);
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
                recyclerViewStrategy.setAdapter(new RectangularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewStrategy);
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
                recyclerViewPopular.setAdapter(new PopularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewPopular);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });

        callActionGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewAction.setAdapter(new RectangularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewAction);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });

        callMultiplayerGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewMulti.setAdapter(new RectangularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewMulti);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
            }
        });

        callOfflineGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewOffline.setAdapter(new RectangularItemAdapter(response.body(),
                        MainActivity.this));
                new PagerSnapHelper().attachToRecyclerView(recyclerViewOffline);
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
