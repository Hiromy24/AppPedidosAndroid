package com.example.apppedidosandroid;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_sheet, container, false);

        ImageButton btnClose = view.findViewById(R.id.closeButton);
        btnClose.setOnClickListener(v -> dismiss());

        TextView email = view.findViewById(R.id.emailProfileTextView);
        TextView username = view.findViewById(R.id.usrTextView);
        TextView signOut = view.findViewById(R.id.signOutTextView);
        signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        signOut.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences(getString(R.string.prefs_file),
                    requireContext().MODE_PRIVATE);
            preferences.edit().clear().apply();
            dismiss();
        });

        SharedPreferences preferences = requireActivity().getSharedPreferences(getString(R.string.prefs_file),
                requireContext().MODE_PRIVATE);
        email.setText(preferences.getString("email", ""));
        username.setText(preferences.getString("username", ""));

        ImageView profileImage = view.findViewById(R.id.profileImageView);
        String photoUrl = preferences.getString("photoUrl", "");

        // Cargar la imagen desde la URL usando Glide
        Glide.with(this)
                .load(photoUrl)
                .transform(new CircleCrop())
                .into(profileImage);


        return view;
    }
}
