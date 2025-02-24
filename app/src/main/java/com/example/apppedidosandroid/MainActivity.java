package com.example.apppedidosandroid;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.RectangularItemAdapter;
import com.example.apppedidosandroid.adapters.SquareItemAdapter;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> loginLauncher;
    RecyclerView recyclerView, recyclerView1;
    MaterialToolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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