package com.example.apppedidosandroid;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.AddressAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AddressesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private FirebaseAuth mAuth;
    private DaoAddress addressDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ImageButton btnBack = findViewById(R.id.backImageButton);

        btnBack.setOnClickListener(v -> finish());

        if (currentUser != null) {
            String email = currentUser.getEmail();

            recyclerView = findViewById(R.id.itemRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            addressDAO = new DaoAddress(this);
            addressList = addressDAO.getAllAddresses(email);

            addressAdapter = new AddressAdapter(addressList);
            recyclerView.setAdapter(addressAdapter);
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }
}