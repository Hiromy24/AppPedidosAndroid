package com.example.apppedidosandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.Game;
import com.example.apppedidosandroid.R;

import java.util.List;

public class PopularItemAdapter extends RecyclerView.Adapter<PopularItemAdapter.ViewHolder> {

    private List<Game> games;

    // Fixed download values for the first 5 items.
    private final String[] fixedDownloads = {"500M+", "10M+", "1B+", "10M+", "50M+"};

    public PopularItemAdapter(List<Game> games) {
        this.games = games;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView gameImageView;
        public TextView gameNameTextView;
        public TextView gameCategoriesTextView;
        public TextView ratingTextView;
        public TextView downloadsTextView;
        public ImageView starImageView;
        public ImageView downloadIconImageView;
        public ImageView headerImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gameImageView = itemView.findViewById(R.id.gameImageView3);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView3);
            gameCategoriesTextView = itemView.findViewById(R.id.gameCategoriesTextView3);
            ratingTextView = itemView.findViewById(R.id.ratingTextView3);
            downloadsTextView = itemView.findViewById(R.id.downloadsTextView);
            starImageView = itemView.findViewById(R.id.imageView3);
            downloadIconImageView = itemView.findViewById(R.id.imageView5);
            headerImageView = itemView.findViewById(R.id.headerImageView);
        }
    }

    @NonNull
    @Override
    public PopularItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popular_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularItemAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameNameTextView.setText(game.getNombre());
        holder.gameCategoriesTextView.setText(game.getCategorias());
        holder.ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));

        if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
            Glide.with(holder.gameImageView.getContext())
                    .load(game.getImagenes().get(0))
                    .into(holder.gameImageView);
            if (game.getHeaderImage() != null) {
                Glide.with(holder.headerImageView.getContext())
                        .load(game.getHeaderImage())
                        .into(holder.headerImageView);
            } else {
                holder.headerImageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_background);
        }

        // Set fixed downloads based on position
        if (position < fixedDownloads.length) {
            holder.downloadsTextView.setText(fixedDownloads[position]);
        } else {
            holder.downloadsTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}