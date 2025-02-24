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
        public TextView nameTextView, phoneTextView, addressLine1TextView, addressLine2TextView;
        public ImageView addressImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressImageView = itemView.findViewById(R.id.addressImage);
            nameTextView = itemView.findViewById(R.id.textView5);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            addressLine1TextView = itemView.findViewById(R.id.textView7);
            addressLine2TextView = itemView.findViewById(R.id.textView8);
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
        holder.addressLine1TextView.setText(address.getAddressLine1());
        holder.addressLine2TextView.setText(address.getAddressLine2());
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }
}
