package com.example.apppedidosandroid.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.model.Game;
import com.example.apppedidosandroid.controller.InstallGameActivity;
import com.example.apppedidosandroid.R;

import java.util.List;

public class SquareItemAdapter extends RecyclerView.Adapter<SquareItemAdapter.ViewHolder> {

    private final List<Game> games;
    private final Context context;

    public SquareItemAdapter(List<Game> games, Context context) {
        this.games = games;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ratingTextView;
        ImageView gameImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.gameName);
            ratingTextView = itemView.findViewById(R.id.gameRating);
            gameImageView = itemView.findViewById(R.id.gameImage);
        }
    }

    @NonNull
    @Override
    public SquareItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.square_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.nameTextView.setText(game.getNombre());
        if (game.getPuntuacion() == 0)
            holder.ratingTextView.setText("No rating (Beta Game)");
        else
            holder.ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));
        // Usar Glide para cargar la imagen
        if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(game.getImagenes().get(0)) // Cargar la primera URL de imagen
                    .into(holder.gameImageView);
        } else {
            // Imagen por defecto si no hay imágenes
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InstallGameActivity.class);
            intent.putExtra("game_nombre", game.getNombre());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}