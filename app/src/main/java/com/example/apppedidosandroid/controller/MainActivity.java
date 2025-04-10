package com.example.apppedidosandroid.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.apppedidosandroid.ApiService;
import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.view.adapters.PopularItemAdapter;
import com.example.apppedidosandroid.view.adapters.RectangularItemAdapter;
import com.example.apppedidosandroid.view.adapters.SquareItemAdapter;
import com.example.apppedidosandroid.model.Game;
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
    ProgressBar progressBar;
    View progressBarContainer;
    int apiCallsPending = 6;
    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout seeMoreStrategy, seeMorePopular, seeMoreAction, seeMoreMulti, seeMoreOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView textView = findViewById(R.id.textView2);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textView.startAnimation(fadeIn);
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

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
        linkSeeMore();
        setSupportActionBar(topAppBar);
        recyclerViewManager();
        fetchRandomGames();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            apiCallsPending = 6; // Reset the number of API calls to wait for
            fetchRandomGames();
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
        } else if (id == R.id.action_cart) {
            if (getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).getString("email", null) == null) {
                Toast.makeText(this, "Sign in to see your cart", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                loginLauncher.launch(intent);
                return true;
            } else {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    void linkComponents() {
        recyclerViewSuggestions = findViewById(R.id.squareItemRecyclerView);
        recyclerViewStrategy = findViewById(R.id.rectangularStrategyItemRecyclerView);
        recyclerViewPopular = findViewById(R.id.popularItemRecyclerView);
        recyclerViewAction = findViewById(R.id.rectangularActionItemRecyclerView);
        recyclerViewMulti = findViewById(R.id.rectangularItemRecyclerView);
        recyclerViewOffline = findViewById(R.id.rectangularOfflineItemRecyclerView);
        topAppBar = findViewById(R.id.topAppBar1);
        progressBar = findViewById(R.id.progressBar);
        progressBarContainer = findViewById(R.id.progressBarContainer);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        seeMoreStrategy = findViewById(R.id.strategy);
        seeMoreAction = findViewById(R.id.action);
        seeMoreMulti = findViewById(R.id.multiplayer);
        seeMoreOffline = findViewById(R.id.offline);
    }

    void linkSeeMore() {
        seeMoreStrategy.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("category", "Strategy");
            startActivity(intent);
        });
        seeMoreAction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("category", "Action");
            startActivity(intent);
        });
        seeMoreMulti.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("category", "Multiplayer");
            startActivity(intent);
        });
        seeMoreOffline.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("category", "Offline");
            startActivity(intent);
        });
    }

    void recyclerViewManager() {
        setRecyclerLayout();
        setRecyclerItemDecoration();
    }

    void setRecyclerLayout() {
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewStrategy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMulti.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOffline.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    void setRecyclerItemDecoration() {
        recyclerViewSuggestions.addItemDecoration(new DividerItemDecoration(recyclerViewSuggestions.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewStrategy.addItemDecoration(new DividerItemDecoration(recyclerViewStrategy.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewPopular.addItemDecoration(new DividerItemDecoration(recyclerViewPopular.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewAction.addItemDecoration(new DividerItemDecoration(recyclerViewAction.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewMulti.addItemDecoration(new DividerItemDecoration(recyclerViewMulti.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerViewOffline.addItemDecoration(new DividerItemDecoration(recyclerViewOffline.getContext(), DividerItemDecoration.HORIZONTAL));
    }

    private void fetchRandomGames() {
        progressBarContainer.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);

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
                .baseUrl(getString(R.string.http))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        request = Map.of("app_name", "");
        request1 = Map.of("category", "Strategy", "n_hits", 12, "free", true);
        request2 = Map.of("app_names", List.of("Among Us", "Bullet Echo", "Roblox", "JCC Pokemon Pocket", "Wild Rift"));
        request4 = Map.of("category", "Action", "n_hits", 12, "free", true);
        request5 = Map.of("category", "Multiplayer", "n_hits", 12, "free", true);
        request6 = Map.of("category", "Offline", "n_hits", 12, "free", true);

        callRandomGames = apiService.getGames(request);
        callStrategyGames = apiService.getGames(request1);
        callPopularGames = apiService.getGameInfo(request2);
        callActionGames = apiService.getGames(request4);
        callMultiplayerGames = apiService.getGames(request5);
        callOfflineGames = apiService.getGames(request6);

        callRandomGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewSuggestions.setAdapter(new SquareItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewSuggestions);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });

        callStrategyGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewStrategy.setAdapter(new RectangularItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewStrategy);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });

        callPopularGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewPopular.setAdapter(new PopularItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewPopular);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });

        callActionGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewAction.setAdapter(new RectangularItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewAction);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });

        callMultiplayerGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewMulti.setAdapter(new RectangularItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewMulti);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });

        callOfflineGames.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Game>> call, @NonNull Response<List<Game>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    response(response.code(), response.message());
                    return;
                }
                recyclerViewOffline.setAdapter(new RectangularItemAdapter(response.body(), MainActivity.this));
                attachSnapHelper(recyclerViewOffline);
                checkApiCallsCompletion();
            }

            @Override
            public void onFailure(@NonNull Call<List<Game>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error obtaining games", Toast.LENGTH_SHORT).show();
                checkApiCallsCompletion();
            }
        });
    }

    private void checkApiCallsCompletion() {
        apiCallsPending--;
        if (apiCallsPending == 0) {
            progressBarContainer.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void attachSnapHelper(RecyclerView recyclerView) {
        if (recyclerView.getOnFlingListener() == null) {
            new PagerSnapHelper().attachToRecyclerView(recyclerView);
        }
    }

    public void showProfileSheet() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        if (preferences.getString("email", null) != null) {
            ProfileSheet profileSheet = new ProfileSheet();
            profileSheet.show(getSupportFragmentManager(), "ProfileSheet");
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            loginLauncher.launch(intent);
        }
        invalidateOptionsMenu(); // Refresh the menu
    }

    void response(int responseCode, String responseMessage) {
        Log.e("API_ERROR", "Response code: " + responseCode);
        Log.e("API_ERROR", "Response message: " + responseMessage);
        Toast.makeText(MainActivity.this, "Games not found", Toast.LENGTH_SHORT).show();
    }

    public Bitmap base64ToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}