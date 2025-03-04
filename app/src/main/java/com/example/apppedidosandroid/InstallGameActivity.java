package com.example.apppedidosandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.adapters.InstallGameAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InstallGameActivity extends AppCompatActivity {

    private TextView gameNameTextView, gameCategoriesTextView, ratingTextView, downloadsTextView,
            descriptionTExtView;
    private ImageView iconImageView;
    private RecyclerView carouselRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.install_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameNameTextView = findViewById(R.id.gameNameTextView3);
        gameCategoriesTextView = findViewById(R.id.gameCategoriesTextView3);
        ratingTextView = findViewById(R.id.ratingTextView3);
        downloadsTextView = findViewById(R.id.downloadsTextView);
        descriptionTExtView = findViewById(R.id.descriptionTextView);
        iconImageView = findViewById(R.id.gameImageView);
        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String gameName = getIntent().getStringExtra("game_nombre");
        fetchGameDetails(gameName);
    }

    private void fetchGameDetails(String gameName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.34.127.121:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Map<String, Object> request = Map.of("app_names", List.of(gameName));
        Call<List<Game>> call = apiService.getGameInfo(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    response(response.code(), response.message());
                    return;
                }
                Game game = response.body().get(0);
                setGameDetails(game);
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(InstallGameActivity.this, "Error obtaining game details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGameDetails(Game game) {
        gameNameTextView.setText(game.getNombre());
        gameCategoriesTextView.setText(game.getCategorias());
        descriptionTExtView.setText(game.getDescripcion());
        ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));
        downloadsTextView.setText("500M+");

        if (game.getHeaderImage() != null) {
            Glide.with(this).load(game.getImagenes().get(0)).into(iconImageView);
        } else {
            iconImageView.setImageResource(R.drawable.ic_launcher_background);
        }

        List<Object> items = new ArrayList<>();
        if (game.getVideo() != null) {
            items.add(game.getVideo()); // Add the video URL
        }
        if (game.getImagenes() != null) {
            items.addAll(game.getImagenes().subList(1, Math.min(game.getImagenes().size(), 10))); // Add up to 8 image URLs
        }

        InstallGameAdapter adapter = new InstallGameAdapter(items, this);
        carouselRecyclerView.setAdapter(adapter);
    }

    void response(int responseCode, String responseMessage) {
        Log.e("API_ERROR", "Response code: " + responseCode);
        Log.e("API_ERROR", "Response message: " + responseMessage);
        Toast.makeText(InstallGameActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
    }
}