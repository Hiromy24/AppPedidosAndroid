package com.example.apppedidosandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAddress extends AppCompatActivity {
    private EditText fullNameEditText, phoneEditText, streetEditText,
            streetNumberEditText, postalCodeEditText, cityEditText, portalEditText;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fullNameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        streetEditText = findViewById(R.id.streetEditText);
        streetNumberEditText = findViewById(R.id.numberEditText);
        portalEditText = findViewById(R.id.portalEditText);
        postalCodeEditText = findViewById(R.id.postalEditText);
        cityEditText = findViewById(R.id.cityEditText);
        saveButton = findViewById(R.id.button);

        saveButton.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String streetNumber = streetNumberEditText.getText().toString().trim();
        String portal = portalEditText.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || streetNumber.isEmpty() ||
                portal.isEmpty() || postalCode.isEmpty() || city.isEmpty()) {
            Toast.makeText(EditAddress.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(EditAddress.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        Address address = new Address(fullName, phone, street, streetNumber, portal, postalCode, city);

        databaseReference.child("users").child(currentUser.getUid())
                .child("addresses").push().setValue(address)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditAddress.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditAddress.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
