package com.example.apppedidosandroid.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.apppedidosandroid.ApiService;
import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.view.adapters.SingleRectangularItemAdapter;
import com.example.apppedidosandroid.model.Game;
import com.google.android.material.appbar.MaterialToolbar;

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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View progressBarContainer;
    private int apiCallsPending = 1; // Number of API calls to wait for

    MaterialToolbar topAppBar;

    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        topAppBar = findViewById(R.id.topAppBar1);
        setSupportActionBar(topAppBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        progressBar = findViewById(R.id.progressBar);
        progressBarContainer = findViewById(R.id.progressBarContainer);

        String gameName = getIntent().getStringExtra("gameNames");
        String category = getIntent().getStringExtra("category");
        fetchGameDetails(gameName, category);
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        showProfileSheet();
                    }
                });
    }

    //region ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        View actionView = getLayoutInflater().inflate(R.layout.menu_profile_image, null);
        profileItem.setActionView(actionView);

        ImageView profileImageView = actionView.findViewById(R.id.profileImageView);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        String email1 = preferences.getString("email", "");
        String photoUrl = preferences.getString("photoUrlBit_" + email1, "");
        if (photoUrl.isEmpty()) {
            photoUrl = preferences.getString("photoUrl_" + email1, "");
            if (!photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .transform(new CircleCrop())
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.circle_user);
            }
        } else {
            Glide.with(this)
                    .load(base64ToBitmap(photoUrl))
                    .transform(new CircleCrop())
                    .into(profileImageView);
        }

        profileImageView.setOnClickListener(v -> showProfileSheet());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
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

    private void fetchGameDetails(String game, String category) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.http))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Game>> callGameDetails;
        Map<String, Object> request;
        if (game != null) {
            request = Map.of("app_name", game, "n_hits", 48, "free", true);
            callGameDetails = apiService.getGames(request);
        } else {
            request = Map.of("category", category, "n_hits", 48, "free", true);
            callGameDetails = apiService.getGames(request);
        }

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
                recyclerView.setAdapter(new SingleRectangularItemAdapter(filteredGames, SearchActivity.this));
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });
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
        Toast.makeText(SearchActivity.this, "Games not found", Toast.LENGTH_SHORT).show();
    }

    public Bitmap base64ToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}