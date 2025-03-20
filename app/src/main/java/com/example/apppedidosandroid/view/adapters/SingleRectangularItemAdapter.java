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

public class SingleRectangularItemAdapter extends RecyclerView.Adapter<SingleRectangularItemAdapter.ViewHolder> {

    private List<Game> games;
    private Context context;

    public SingleRectangularItemAdapter(List<Game> games, Context context) {
        this.games = games;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, categoryTextView, ratingTextView;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gameImageView1);
            nameTextView = itemView.findViewById(R.id.gameNameTextView1);
            categoryTextView = itemView.findViewById(R.id.gameCategoriesTextView1);
            ratingTextView = itemView.findViewById(R.id.ratingTextView1);
        }
    }

    @NonNull
    @Override
    public SingleRectangularItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_rectangular_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleRectangularItemAdapter.ViewHolder holder, int position) {
        bindGame(holder, position);
    }

    private void bindGame(ViewHolder holder, int index) {
        if (index < games.size()) {
            Game game = games.get(index);
            holder.nameTextView.setText(game.getNombre());
            holder.categoryTextView.setText(game.getCategorias());
            if (game.getPuntuacion() == 0)
                holder.ratingTextView.setText(R.string.no_rating_beta_game);
            else
                holder.ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));
            if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
                Glide.with(holder.imageView.getContext())
                        .load(game.getImagenes().get(0))
                        .into(holder.imageView);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, InstallGameActivity.class);
                intent.putExtra("game_nombre", game.getNombre());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}