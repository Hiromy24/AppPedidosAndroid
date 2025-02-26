package com.example.apppedidosandroid;

import static android.app.Activity.RESULT_OK;

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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;

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
        btnClose.setOnClickListener(v -> dismiss());
        FrameLayout photo = view.findViewById(R.id.photoLayout);
        preferences = requireActivity().getSharedPreferences(getString(R.string.prefs_file),
                requireContext().MODE_PRIVATE);
        ImageView profileImage = view.findViewById(R.id.profileImageView);

        photo.setOnClickListener(v -> {
            if (checkPermissions()) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoAction.launch(takePictureIntent);
            } else {
                requestPermissions();
            }
        });

        photoAction = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        Glide.with(this)
                                .load(imageBitmap)
                                .transform(new CircleCrop())
                                .into(profileImage);
                        String email = preferences.getString("email", "");
                        preferences.edit().putString("photoUrlBit_" + email, bitmapToBase64(imageBitmap)).apply();
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
        signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        signOut.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
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

        LinearLayout direction = view.findViewById(R.id.directionLayout);

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
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }
}