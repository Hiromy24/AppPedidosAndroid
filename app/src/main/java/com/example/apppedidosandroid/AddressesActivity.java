package com.example.apppedidosandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.adapters.AddressAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddressesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        recyclerView = findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        // Agrega elementos a la lista de direcciones
        addressList.add(new Address("Juan David", "+34-578945320", "Calle Aguja, N80, 4G", "54320, Parla-Valencia"));
        // Agrega más elementos según sea necesario

        addressAdapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(addressAdapter);
    }
}