package com.example.apppedidosandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.R;

public class SquareItemAdapter extends RecyclerView.Adapter<SquareItemAdapter.ViewHolder> {

    private final String[] items;
    private final int[] images;

    public SquareItemAdapter(String[] items, int[] images) {
        this.items = items;
        this.images = images;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImageView);
            textView = itemView.findViewById(R.id.productTextView);
        }
    }
    @NonNull
    @Override
    public SquareItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.square_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SquareItemAdapter.ViewHolder holder, int position) {
        holder.textView.setText(items[position]);
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
