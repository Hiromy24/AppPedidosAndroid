package com.example.apppedidosandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private ActivityResultLauncher<Intent> signInGoogleLauncher;
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

        //region Launcher for Google Sign In
        signInGoogleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            AuthCredential credential = GoogleAuthProvider
                                    .getCredential(account.getIdToken(), null);
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener(this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseUser user = FirebaseAuth.getInstance()
                                                    .getCurrentUser();
                                            Toast.makeText(this, "Google sign in successful",
                                                    Toast.LENGTH_SHORT).show();
                                            if (user != null) {
                                                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("email", user.getEmail());
                                                editor.putString("username", user.getDisplayName());
                                                editor.putString("photoUrl_" + user.getEmail(),
                                                        account.getPhotoUrl().toString()).apply();
                                                editor.apply();
                                            }
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
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
        //endregion

        btnRegister.setOnClickListener(v -> {
            if (!etEmail.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(etEmail.getText().toString(),
                                etPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Register completed",
                                        Toast.LENGTH_SHORT).show();

                                AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                                        .setTitle("Register successful")
                                        .setMessage("Please, sign in with your new account")
                                        .setPositiveButton("Accept", (dialogInterface, which) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .setOnDismissListener(dialogInterface -> {
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        })
                                        .create();

                                dialog.setOnShowListener(dialogInterface -> {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                            .setTextColor(ContextCompat
                                                    .getColor(this, android.R.color.black));
                                });

                                dialog.show();
                            } else {
                                Toast.makeText(this, "Error on register",
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
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", user.getEmail());
                                    editor.apply();
                                }
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
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
                signInGoogleLauncher.launch(signInIntent);
        });
    }
}