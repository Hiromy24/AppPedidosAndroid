package com.example.apppedidosandroid.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.model.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAddress extends AppCompatActivity {
    private EditText fullNameEditText, phoneEditText, streetEditText,
            streetNumberEditText, postalCodeEditText, cityEditText, portalEditText;
    private Button saveButton;

    private ImageButton backButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    // EditAddress.java
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
        saveButton = findViewById(R.id.saveAddressButton);
        backButton = findViewById(R.id.backImageButton);

        Address address = (Address) getIntent().getSerializableExtra("address");
        String addressId = getIntent().getStringExtra("addressId");

        if (address != null) {
            fullNameEditText.setText(address.getFullName());
            phoneEditText.setText(address.getPhone());
            streetEditText.setText(address.getStreet());
            streetNumberEditText.setText(address.getStreetNumber());
            portalEditText.setText(address.getPortal());
            postalCodeEditText.setText(address.getPostalCode());
            cityEditText.setText(address.getCity());
        }

        saveButton.setOnClickListener(v -> showConfirmationDialog(addressId));
        backButton.setOnClickListener(v -> showExitDialog());
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_exit_dialog, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        TextView message = dialogView.findViewById(R.id.dialogMessage);
        Button acceptButton = dialogView.findViewById(R.id.aceptarDialog);
        Button cancelButton = dialogView.findViewById(R.id.cancelarDialog);

        title.setText(R.string.confirm_exit);
        message.setText(R.string.exit_or_continue);

        AlertDialog dialog = builder.create();

        acceptButton.setOnClickListener(v -> finish());
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showConfirmationDialog(String addressId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_save_dialog, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        TextView message = dialogView.findViewById(R.id.dialogMessage);
        Button acceptButton = dialogView.findViewById(R.id.aceptarDialog);
        Button cancelButton = dialogView.findViewById(R.id.cancelarDialog);

        title.setText(R.string.confirm_save);
        message.setText(R.string.save_or_continue);

        AlertDialog dialog = builder.create();

        acceptButton.setOnClickListener(v -> saveAddress(addressId));
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveAddress(String addressId) {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String streetNumber = streetNumberEditText.getText().toString().trim();
        String portal = portalEditText.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || streetNumber.isEmpty()
                || portal.isEmpty() || postalCode.isEmpty() || city.isEmpty()) {
            Toast.makeText(EditAddress.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(EditAddress.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userAddressesRef = databaseReference.child("users").child(currentUser.getUid()).child("addresses");

        if (addressId == null || addressId.isEmpty()) {
            addressId = userAddressesRef.push().getKey();
        }

        Address address = new Address(addressId, fullName, phone, street, streetNumber, portal, postalCode, city);

        userAddressesRef.child(addressId).setValue(address)
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
