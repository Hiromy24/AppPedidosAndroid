package com.example.apppedidosandroid;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
            return true;
        } else if (id == R.id.action_profile) {
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
        //endregion
        recyclerView.setAdapter(new SquareItemAdapter(items, images));
        recyclerView1.setAdapter(new RectangularItemAdapter(items, images, categories, rating));
    }
    //endregion
}