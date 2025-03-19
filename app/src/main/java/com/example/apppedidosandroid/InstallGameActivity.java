// InstallGameActivity.java
package com.example.apppedidosandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
    private ProgressBar progressBar;
    private View progressBarContainer;
    private Button installButton;
    private CartManager cartManager;
    private Game currentGame;

    private int apiCallsPending = 1; // Number of API calls to wait for

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

        progressBar = findViewById(R.id.progressBar);
        progressBarContainer = findViewById(R.id.progressBarContainer);
        installButton = findViewById(R.id.installButton);

        // Ensure the ProgressBar is visible at the start
        progressBarContainer.setVisibility(View.VISIBLE);

        String gameName = getIntent().getStringExtra("game_nombre");
        fetchGameDetails(gameName);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartManager = new CartManager(currentUser.getEmail());
        }

        installButton.setOnClickListener(v -> {
            if (getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).getString("email", null) == null) {
                Toast.makeText(this, "You must be logged in to install games", Toast.LENGTH_SHORT).show();
            } else {
                if (currentGame != null) {
                    boolean isInstalled = currentGame.isInstalled();
                    currentGame.setInstalled(!isInstalled);
                    cartManager.saveCart(List.of(currentGame));
                    installButton.setText(currentGame.isInstalled() ? R.string.uninstall : R.string.install);
                    Toast.makeText(this, currentGame.isInstalled() ? R.string.installed : R.string.uninstalled, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGameInstallationStatus();
    }

    private void checkGameInstallationStatus() {
        if (currentGame != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                cartManager = new CartManager(currentUser.getEmail());
                cartManager.getCartReference().child(currentGame.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean installed = dataSnapshot.exists() && dataSnapshot.child("installed").getValue(Boolean.class);
                        installButton.setText(installed ? R.string.uninstall : R.string.install);
                        currentGame.setInstalled(installed);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Error checking if game is installed", databaseError.toException());
                    }
                });
            }
        }
    }

    private void fetchGameDetails(String gameName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.http))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Map<String, Object> request = Map.of("app_names", List.of(gameName), "free", true, "n_hits", 21);
        Call<List<Game>> call = apiService.getGameInfo(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    response(response.code(), response.message());
                    return;
                }
                currentGame = response.body().get(0);
                setGameDetails(currentGame);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(InstallGameActivity.this, "Error obtaining game details", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });
    }

    private void setGameDetails(Game game) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        gameNameTextView.setText(game.getNombre());
        gameCategoriesTextView.setText(game.getCategorias());
        descriptionTExtView.setText(game.getDescripcion());
        ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));
        downloadsTextView.setText(game.getDescargas());

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
            items.addAll(game.getImagenes().subList(1, Math.min(game.getImagenes().size(), 10)));
        }

        InstallGameAdapter adapter = new InstallGameAdapter(items, this);
        carouselRecyclerView.setAdapter(adapter);

        // Check if the game is installed in Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartManager = new CartManager(currentUser.getEmail());
            cartManager.getCartReference().child(game.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean installed = dataSnapshot.exists() && dataSnapshot.child("installed").getValue(Boolean.class);
                    installButton.setText(installed ? R.string.uninstall : R.string.install);
                    currentGame.setInstalled(installed);
                    game.setInstalled(installed);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error checking if game is installed", databaseError.toException());
                }
            });
        }
    }

    private void checkApiCallsCompletion() {
        apiCallsPending--;
        if (apiCallsPending == 0) {
            progressBarContainer.setVisibility(View.GONE);
        }
    }

    void response(int responseCode, String responseMessage) {
        Log.e("API_ERROR", "Response code: " + responseCode);
        Log.e("API_ERROR", "Response message: " + responseMessage);
        Toast.makeText(InstallGameActivity.this, "No se encontraron juegos", Toast.LENGTH_SHORT).show();
        progressBarContainer.setVisibility(View.GONE);
    }
}