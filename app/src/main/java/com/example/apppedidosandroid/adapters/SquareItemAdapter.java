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

public class SquareItemAdapter extends RecyclerView.Adapter<SquareItemAdapter.ViewHolder> {

    private List<Game> games;

    public SquareItemAdapter(List<Game> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate del layout para cada item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.square_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.nameTextView.setText(game.getNombre());
        holder.descriptionTextView.setText(game.getDescripcion());
        holder.ratingTextView.setText(String.format("%.1f", game.getPuntuacion())); // Format the rating to one decimal place

        // Usar Glide para cargar la primera imagen de la lista de imágenes del juego
        if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(game.getImagenes().get(0)) // Cargar la primera URL de imagen
                    .into(holder.gameImageView);
        } else {
            // Imagen por defecto en caso de que no haya imágenes
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView, ratingTextView;
        ImageView gameImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.gameName);
            descriptionTextView = itemView.findViewById(R.id.gameDescription);
            ratingTextView = itemView.findViewById(R.id.gameRating);
            gameImageView = itemView.findViewById(R.id.gameImage);
        }
    }
}
