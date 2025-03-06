package com.example.apppedidosandroid.adapters;

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
import com.example.apppedidosandroid.Game;
import com.example.apppedidosandroid.InstallGameActivity;
import com.example.apppedidosandroid.R;

import java.util.List;

public class RectangularItemAdapter extends RecyclerView.Adapter<RectangularItemAdapter.ViewHolder> {

    private List<Game> games;
    private Context context;

    public RectangularItemAdapter(List<Game> games, Context context) {
        this.games = games;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView1, categoryTextView1, ratingTextView1;
        public TextView textView2, categoryTextView2, ratingTextView2;
        public TextView textView3, categoryTextView3, ratingTextView3;
        public ImageView imageView1, imageView2, imageView3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.gameImageView1);
            textView1 = itemView.findViewById(R.id.gameNameTextView1);
            categoryTextView1 = itemView.findViewById(R.id.gameCategoriesTextView1);
            ratingTextView1 = itemView.findViewById(R.id.ratingTextView1);

            imageView2 = itemView.findViewById(R.id.gameImageView2);
            textView2 = itemView.findViewById(R.id.gameNameTextView2);
            categoryTextView2 = itemView.findViewById(R.id.gameCategoriesTextView2);
            ratingTextView2 = itemView.findViewById(R.id.ratingTextView2);

            imageView3 = itemView.findViewById(R.id.gameImageView3);
            textView3 = itemView.findViewById(R.id.gameNameTextView3);
            categoryTextView3 = itemView.findViewById(R.id.gameCategoriesTextView3);
            ratingTextView3 = itemView.findViewById(R.id.ratingTextView3);
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
        int baseIndex = position * 3;

        bindGame(holder.imageView1, holder.textView1, holder.categoryTextView1, holder.ratingTextView1, baseIndex);
        bindGame(holder.imageView2, holder.textView2, holder.categoryTextView2, holder.ratingTextView2, baseIndex + 1);
        bindGame(holder.imageView3, holder.textView3, holder.categoryTextView3, holder.ratingTextView3, baseIndex + 2);
    }

    private void bindGame(ImageView imageView, TextView nameTextView, TextView categoryTextView, TextView ratingTextView, int index) {
        if (index < games.size()) {
            Game game = games.get(index);
            nameTextView.setText(game.getNombre());
            categoryTextView.setText(game.getCategorias());
            ratingTextView.setText(String.format("%.1f", game.getPuntuacion()));

            if (game.getImagenes() != null && !game.getImagenes().isEmpty()) {
                Glide.with(imageView.getContext())
                        .load(game.getImagenes().get(0))
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, InstallGameActivity.class);
                intent.putExtra("game_nombre", game.getNombre());
                context.startActivity(intent);
            });
        } else {
            nameTextView.setText("");
            categoryTextView.setText("");
            ratingTextView.setText("");
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil(games.size() / 3.0);
    }
}