package com.example.apppedidosandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.adapters.AddressAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddressesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(addressAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadDataFromFirebase();

        Button addDirectionButton = findViewById(R.id.addDirectionButton);
        addDirectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddressesActivity.this, EditAddress.class);
            startActivity(intent);
        });
    }
    private void loadDataFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        databaseReference.child("users").child(currentUser.getUid())
                .child("addresses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        addressList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Address address = snapshot.getValue(Address.class);
                            if (address != null) {
                                addressList.add(address);
                            }
                        }
                        addressAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddressesActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
