package com.example.finalproject_sudokugame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText login_etUserName;
    EditText login_etPassword;
    Button login_btnLogin;
    Button login_btnRegistration;
    TextView login_tvLogin;
    Toolbar login_toolbar;

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
        login_etUserName = findViewById(R.id.login_etUserName);
        login_etPassword = findViewById(R.id.login_etPassword);
        login_btnLogin = findViewById(R.id.login_btnLogin);
        login_btnRegistration = findViewById(R.id.login_btnRegistration);
        login_tvLogin = findViewById(R.id.login_tvLogin);
        login_toolbar = findViewById(R.id.login_toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("sudoku_user", Context.MODE_PRIVATE);

        login_btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_etUserName.getText().toString().trim();
                String password = login_etPassword.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });


        login_btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_etUserName.getText().toString().trim();
                String password = login_etPassword.getText().toString().trim();

                String savedUsername = sharedPreferences.getString("username", null);
                String savedPassword = sharedPreferences.getString("password", null);

                if (username.equals(savedUsername) && password.equals(savedPassword)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            return true;
        }

        if (id == R.id.menu_settings) {
            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
