package com.example.apppedidosandroid.controller;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.view.adapters.CartAdapter;
import com.example.apppedidosandroid.model.CartManager;
import com.example.apppedidosandroid.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerViewGames;
    private List<Game> gamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewGames = findViewById(R.id.recyclerViewGames);
        recyclerViewGames.setLayoutManager(new LinearLayoutManager(this));

        gamesList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            cartManager = new CartManager(currentUser.getEmail());
        }

        cartAdapter = new CartAdapter(this, gamesList, cartManager);
        recyclerViewGames.setAdapter(cartAdapter);

        loadCartData();
    }

    private void loadCartData() {
        if (cartManager == null) return;

        cartManager.getCartReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Game game = snapshot.getValue(Game.class);
                    if (game != null) {
                        gamesList.add(game);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                if (gamesList.isEmpty()) {
                    findViewById(R.id.tvNoGames).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.tvNoGames).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}