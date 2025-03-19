// CartManager.java
package com.example.apppedidosandroid;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartManager {
    private String userEmail;
    private DatabaseReference databaseReference;

    public CartManager(String userEmail) {
        this.userEmail = userEmail;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("historial_carrito");
        }
    }

    public void saveCart(List<Game> games) {
        if (databaseReference != null) {
            for (Game game : games) {
                databaseReference.child(game.getNombre()).setValue(game);
            }
        }
    }

    public DatabaseReference getCartReference() {
        return databaseReference;
    }
}