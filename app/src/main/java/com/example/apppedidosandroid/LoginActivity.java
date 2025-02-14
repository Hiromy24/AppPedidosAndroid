package com.example.apppedidosandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnRegister;

    EditText etEmail;
    EditText etPassword;

    com.google.android.gms.common.SignInButton googleSignInButton;
    private ActivityResultLauncher<Intent> signInLauncher;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.registerButton);
        etEmail = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.pwdEditText);
        googleSignInButton = findViewById(R.id.sign_in_button);

        // Register the launcher
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener(this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("email", user.getEmail());
                                                editor.putString("provider", "GOOGLE");
                                                editor.apply();
                                            }
                                            Toast.makeText(this, "Google sign in successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed", e);
                        }
                    }
                }
        );

        btnRegister.setOnClickListener(v -> {
            if (!etEmail.getText().toString().isEmpty() &&
                    !etPassword.getText().toString().isEmpty()) {
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword
                                (etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Completed registration",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error on registration",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        btnLogin.setOnClickListener(k -> {
            if (!etEmail.getText().toString().isEmpty() &&
                    !etPassword.getText().toString().isEmpty()) {
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword
                                (etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Completed login",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No user or email login",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        googleSignInButton.setOnClickListener(v -> {
                GoogleSignInOptions gso = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                com.google.android.gms.auth.api.signin.GoogleSignInClient googleSignInClient =
                        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInLauncher.launch(signInIntent);
        });
    }
}