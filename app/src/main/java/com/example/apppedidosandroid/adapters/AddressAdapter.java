package com.example.apppedidosandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<Address> addressList;

    public AddressAdapter(List<Address> addressList) {
        this.addressList = addressList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, phoneTextView, streetTextView, streetNumberTextView,
                portalTextView, postalCodeTextView, cityProvinceTextView;

        public ImageView addressImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressImageView = itemView.findViewById(R.id.addressImage);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            streetTextView = itemView.findViewById(R.id.streetTextView);
            streetNumberTextView = itemView.findViewById(R.id.numbertextView);
            portalTextView = itemView.findViewById(R.id.portalTextView);
            postalCodeTextView = itemView.findViewById(R.id.postalTextView);
            cityProvinceTextView = itemView.findViewById(R.id.cityProvincetextView);
        }
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.nameTextView.setText(address.getName());
        holder.phoneTextView.setText(address.getPhone());
        holder.streetTextView.setText(address.getStreet());
        holder.streetNumberTextView.setText(address.getStreetNumber());
        holder.portalTextView.setText(address.getPortal());
        holder.postalCodeTextView.setText(address.getPostalCode());
        holder.cityProvinceTextView.setText(address.getCityProvince());
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }
}