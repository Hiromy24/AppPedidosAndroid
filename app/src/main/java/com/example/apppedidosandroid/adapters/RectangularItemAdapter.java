/*
package com.example.apppedidosandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.R;

import java.util.List;

public class RectangularItemAdapter extends RecyclerView.Adapter<RectangularItemAdapter.ViewHolder> {

    private final List<String> items;
    private final List<String> categories;
    private final List<Double> ratings;
    private final List<String> imageUrls;

    public RectangularItemAdapter(List<String> items, List<String> imageUrls, List<String> categories, List<Double> ratings) {
        this.items = items;
        this.imageUrls = imageUrls;
        this.categories = categories;
        this.ratings = ratings;
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
        holder.textView.setText(items.get(position));
        holder.categoryTextView.setText(categories.get(position));
        holder.ratingTextView.setText(String.valueOf(ratings.get(position)));
        Glide.with(holder.imageView.getContext()).load(imageUrls.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}*/
