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

public class RectangularItemAdapter extends RecyclerView.Adapter<RectangularItemAdapter.ViewHolder> {

    private final List<Game> games;

    public RectangularItemAdapter(List<Game> games) {

        this.games = games;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView, categoryTextView, ratingTextView;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gameImageView);
            textView = itemView.findViewById(R.id.gameNameTextView);
            categoryTextView = itemView.findViewById(R.id.gameCategoriesTextView);
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
        Game game = games.get(position);
        holder.textView.setText(game.getNombre());
        holder.ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));

        // Usar Glide para cargar la imagen
        if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(game.getImagenes().get(0)) // Cargar la primera URL de imagen
                    .into(holder.imageView);
        } else {
            // Imagen por defecto si no hay imágenes
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
