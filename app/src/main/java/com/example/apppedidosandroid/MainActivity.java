package com.example.apppedidosandroid;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Intent> loginLauncher;
    RecyclerView recyclerView, recyclerView1;
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

        fetchRandomGames(); // Llamar al método que obtiene los juegos
        linkComponents();
        setSupportActionBar(topAppBar);
        recyclerViewManager();
    }
    void linkComponents(){
        recyclerView = findViewById(R.id.squareItemRecyclerView);
        recyclerView1 = findViewById(R.id.rectangularItemRecyclerView);
        topAppBar = findViewById(R.id.topAppBar);
    }
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
    //region RecyclerView
    void recyclerViewManager(){
        setRecyclerLayout();
        setRecyclerItemDecoration();
        setRecyclerAdapter();
    }
    void setRecyclerLayout(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
    void setRecyclerItemDecoration(){
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView1.addItemDecoration(new DividerItemDecoration(recyclerView1.getContext(),DividerItemDecoration.VERTICAL));
    }
    void setRecyclerAdapter(){
        //region ARRAYS

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Login succeed", Toast.LENGTH_SHORT).show();
                        showProfileSheet();
                    }
                });


        //endregion
    }

    public void showProfileSheet() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file),
                MODE_PRIVATE);
        if (preferences.getString("email", null) != null) {
            ProfileSheet profileSheet = new ProfileSheet();
            profileSheet.show(getSupportFragmentManager(), "ProfileSheet");
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            loginLauncher.launch(intent);
        }
    }

}