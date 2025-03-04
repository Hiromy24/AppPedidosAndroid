package com.example.apppedidosandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apppedidosandroid.R;

import java.util.List;

public class InstallGameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_IMAGE = 1;
    private List<Object> items;
    private Context context;

    public InstallGameAdapter(List<Object> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_VIDEO;
        } else {
            return TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_item, parent, false);
            return new VideoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_item, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_VIDEO) {
            VideoViewHolder videoHolder = (VideoViewHolder) holder;
            String videoUrl = (String) items.get(position);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                videoHolder.webView.setWebViewClient(new WebViewClient());
                WebSettings webSettings = videoHolder.webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                videoHolder.webView.loadUrl(videoUrl + "&autoplay=1");
            }
        } else {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            int imagePosition = position - 1; // Adjust position to skip the first item
            if (imagePosition < items.size() && items.get(imagePosition) instanceof String) {
                String imageUrl = (String) items.get(imagePosition);
                Glide.with(imageHolder.imageView.getContext())
                        .load(imageUrl)
                        .into(imageHolder.imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public WebView webView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webView);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}