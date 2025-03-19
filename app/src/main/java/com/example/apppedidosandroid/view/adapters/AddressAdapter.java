package com.example.apppedidosandroid.view.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.controller.EditAddress;
import com.example.apppedidosandroid.R;
import com.example.apppedidosandroid.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<Address> addressList;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(Address address);
    }

    public AddressAdapter(List<Address> addressList, OnItemLongClickListener longClickListener) {
        this.addressList = addressList;
        this.longClickListener = longClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, phoneTextView, streetTextView, postalCodeTextView, cityProvinceTextView;
        public ImageView addressImageView;

        public Button editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressImageView = itemView.findViewById(R.id.addressImage);
            streetTextView = itemView.findViewById(R.id.streetTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            streetTextView = itemView.findViewById(R.id.streetTextView);
            postalCodeTextView = itemView.findViewById(R.id.postalTextView);
            cityProvinceTextView = itemView.findViewById(R.id.cityProvincetextView);
            editButton = itemView.findViewById(R.id.editButton);
        }

        public void bind(final Address address, final OnItemLongClickListener longClickListener) {
            itemView.setOnLongClickListener(v -> {
                showPopupMenu(v, address, longClickListener);
                return true;
            });
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditAddress.class);
                intent.putExtra("address", address);
                intent.putExtra("addressId", address.getId());
                v.getContext().startActivity(intent);
            });
        }

        private void showPopupMenu(View view, Address address, OnItemLongClickListener longClickListener) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.address_context_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete_address) {
                    longClickListener.onItemLongClick(address);
                    return true;
                }
                return false;
            });
            popupMenu.show();
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
        holder.nameTextView.setText(address.getFullName());
        holder.phoneTextView.setText(address.getPhone());
        holder.streetTextView.setText(String.format("%s | %s | %s", address.getStreet(), address.getStreetNumber(), address.getPortal()));
        holder.postalCodeTextView.setText(address.getPostalCode());
        holder.cityProvinceTextView.setText(address.getCity());
        holder.bind(address, longClickListener);
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }
}