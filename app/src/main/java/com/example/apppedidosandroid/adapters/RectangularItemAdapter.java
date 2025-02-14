package com.example.apppedidosandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppedidosandroid.R;

public class RectangularItemAdapter extends RecyclerView.Adapter<RectangularItemAdapter.ViewHolder> {

    private final String[] items;
    private final String[] categories;
    private final double[] rating;
    private final int[] images;

    public RectangularItemAdapter(String[] items, int[] images, String[] categories, double[] rating) {
        this.items = items;
        this.images = images;
        this.categories = categories;
        this.rating = rating;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView, categoryTextView, ratingTextView;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImageView);
            textView = itemView.findViewById(R.id.productNameTextView);
            categoryTextView = itemView.findViewById(R.id.productCategoriesTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
    @NonNull
    @Override
    public RectangularItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rectangular_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RectangularItemAdapter.ViewHolder holder, int position) {
        holder.textView.setText(items[position]);
        holder.imageView.setImageResource(images[position]);
        holder.categoryTextView.setText(categories[position]);
        holder.ratingTextView.setText(String.valueOf(rating[position]));
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
