package com.example.apppedidosandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.CartManager;
import com.example.apppedidosandroid.Game;
import com.example.apppedidosandroid.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Game> games;
    private CartManager cartManager;

    public CartAdapter(Context context, List<Game> games, CartManager cartManager) {
        this.context = context;
        this.games = games;
        this.cartManager = cartManager;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Game game = games.get(position);

        holder.gameNameTextView.setText(game.getNombre());
        holder.gameStatusTextView.setText("Status: " + (game.isInstalled() ?
                context.getString(R.string.installed) : context.getString(R.string.not_installed)));

        // Load image from URL using Glide
        Glide.with(context)
                .load(game.getImagenes().get(0))
                .placeholder(R.drawable.star_solid)
                .into(holder.gameImage);

        holder.btnInstall.setText(game.isInstalled() ? R.string.uninstall : R.string.install);
        holder.btnInstall.setOnClickListener(v -> {
            game.setInstalled(!game.isInstalled());
            cartManager.saveCart(games);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView gameNameTextView, gameStatusTextView;
        ImageView gameImage;
        Button btnInstall;

        public CartViewHolder(View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
            gameStatusTextView = itemView.findViewById(R.id.gameStatusTextView);
            gameImage = itemView.findViewById(R.id.gameImageViewView);
            btnInstall = itemView.findViewById(R.id.installButton2);
        }
    }
}