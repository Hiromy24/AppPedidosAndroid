package com.example.apppedidosandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

    private ImageButton backButton;
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
        addressAdapter = new AddressAdapter(addressList, this::showDeleteConfirmationDialog);
        recyclerView.setAdapter(addressAdapter);
        backButton = findViewById(R.id.backImageButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadDataFromFirebase();

        Button addDirectionButton = findViewById(R.id.addDirectionButton);
        addDirectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddressesActivity.this, EditAddress.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(v -> finish());
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

    private void showDeleteConfirmationDialog(Address address) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialogInterface, which) -> deleteAddress(address))
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat
                    .getColor(this, android.R.color.holo_red_dark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat
                    .getColor(this, android.R.color.black));
        });

        dialog.show();
    }

    private void deleteAddress(Address address) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        databaseReference.child("users").child(currentUser.getUid())
                .child("addresses").orderByChild("fullName").equalTo(address.getFullName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                        Toast.makeText(AddressesActivity.this, "Address deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddressesActivity.this, "Failed to delete address", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}