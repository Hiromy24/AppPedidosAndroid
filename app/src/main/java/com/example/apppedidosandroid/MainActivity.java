package com.example.apppedidosandroid;

import android.animation.ObjectAnimator;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String[] items = {"Girasol", "Geranio","Amapola","Violeta","Rosa","Clavel","Flor de Sauco",
                "Flor de lis","Lirio","Flor de loto","Gardenia","flor de las nieves","Margarita"};

        int[] images = {
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background
        };

        String[] categories = {"Carne, Pan", "Queso, Masa",
                "Papas, Frito", "Salchicha, Pan", "Carne, Pan", "Queso, Masa", "Papas, Frito",
                "Salchicha,Pan", "Carne, Pan",
                "Queso, Masa", "Papas, Frito","Salchicha, Pan", "Carne, Pan"};
        double[] rating = {4.5, 4.8, 4.2, 4.0, 4.5, 4.8, 4.2, 4.0, 4.5, 4.8, 4.2, 4.0, 4.0};

        RecyclerView rv =findViewById(R.id.squareItemRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new SquareItemAdapter(items, images));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        RecyclerView rv2 =findViewById(R.id.rectangularItemRecyclerView);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv2.setLayoutManager(layoutManager2);
        rv2.setAdapter(new RectangularItemAdapter(items, images, categories, rating));
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(rv2.getContext(),
                layoutManager2.getOrientation());
        rv2.addItemDecoration(dividerItemDecoration2);


    }
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
            // Acción para el botón de perfil
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}