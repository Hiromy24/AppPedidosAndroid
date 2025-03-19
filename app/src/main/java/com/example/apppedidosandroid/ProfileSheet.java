package com.example.apppedidosandroid;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

public class ProfileSheet extends BottomSheetDialogFragment {
    private ActivityResultLauncher<Intent> photoAction;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    SharedPreferences preferences;
    String photoUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_sheet, container, false);
        ImageButton btnClose = view.findViewById(R.id.closeButton);
        FrameLayout photo = view.findViewById(R.id.photoLayout);
        ImageView profileImage = view.findViewById(R.id.profileImageView);
        preferences = requireActivity().getSharedPreferences(getString(R.string.prefs_file),
                MODE_PRIVATE);
        ImageButton directionBtn = view.findViewById(R.id.directionImageButton);
        TextView direction = view.findViewById(R.id.directionTextView);

        btnClose.setOnClickListener(v -> dismiss());
        photo.setOnClickListener(v -> {
            if (checkPermissions()) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoAction.launch(takePictureIntent);
            } else {
                requestPermissions();
            }
        });
        direction.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddressesActivity.class);
            startActivity(intent);
        });

        directionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddressesActivity.class);
            startActivity(intent);
        });

        photoAction = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras())
                                .get("data");
                        Glide.with(this)
                                .load(imageBitmap)
                                .transform(new CircleCrop())
                                .into(profileImage);
                        String email = preferences.getString("email", "");
                        assert imageBitmap != null;
                        preferences.edit().putString("photoUrlBit_" + email,
                                bitmapToBase64(imageBitmap)).apply();
                        preferences.edit().putString("photoUrl_" + email, "").apply();
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        photoAction.launch(takePictureIntent);
                    } else {
                        Toast.makeText(requireContext(), "Permission Denied, " +
                                "You cannot edit your photo.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        TextView email = view.findViewById(R.id.emailProfileTextView);
        TextView username = view.findViewById(R.id.usrTextView);
        TextView signOut = view.findViewById(R.id.signOutTextView);
        TextView themeLabel = view.findViewById(R.id.changeColorTextView);
        signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Button btnToggleTheme = view.findViewById(R.id.toggleThemeButton);

        SharedPreferences themePrefs = requireActivity().getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean("isDarkMode", false);

        themeLabel.setText(isDarkMode ? "Cambiar a Modo Claro" : "Cambiar a Modo Oscuro");

        btnToggleTheme.setOnClickListener(v -> {
            boolean newThemeState = !isDarkMode;
            SharedPreferences.Editor editor = themePrefs.edit();
            editor.putBoolean("isDarkMode", newThemeState);
            editor.apply();

            // Aplicar el cambio de tema
            AppCompatDelegate.setDefaultNightMode(
                    newThemeState ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Cambiar el texto del botón
            themeLabel.setText(newThemeState ? "Cambiar a Modo Claro" : "Cambiar a Modo Oscuro");
        });

        signOut.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(
                    getString(R.string.prefs_file), MODE_PRIVATE);
            prefs.edit().putString("email", null).apply();
            prefs.edit().putString("username", "").apply();
            dismiss();
        });

        email.setText(preferences.getString("email", ""));
        username.setText(preferences.getString("username", ""));

        String email1 = preferences.getString("email", "");
        photoUrl = preferences.getString("photoUrlBit_" + email1, "");
        if (photoUrl.isEmpty()) {
            photoUrl = preferences.getString("photoUrl_" + email1, "");
            if (!photoUrl.isEmpty())
                Glide.with(this)
                        .load(photoUrl)
                        .transform(new CircleCrop())
                        .into(profileImage);
        } else {
            // Load the image from the URL using Glide
            Glide.with(this)
                    .load(base64ToBitmap(photoUrl))
                    .transform(new CircleCrop())
                    .into(profileImage);
        }

        return view;
    }

    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap base64ToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

}