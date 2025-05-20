package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.MainActivity;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.utils.SessionManager;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrDui, etPassword;
    private Button btnLogin, btnRegister;
    private LoginService loginService;
    private SessionManager sessionManager;

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

        // Inicializar vistas y servicios
        etEmailOrDui = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        loginService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL).create(LoginService.class);
        sessionManager = new SessionManager(this);

        // Si ya hay token guardado, ir a MainActivity
        if (sessionManager.getAuthToken() != null) {
            goToMainActivity();
        }

        btnLogin.setOnClickListener(v -> performLogin());

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void performLogin() {
        String emailOrDui = etEmailOrDui.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (emailOrDui.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = isEmail(emailOrDui) ? emailOrDui : "";
        String dui = isDui(emailOrDui) ? emailOrDui : "";

        loginService.login(new LoginRequest(email, dui, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();
                            sessionManager.saveAuthToken(token);

                            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            handleServerError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
    }

    private boolean isEmail(String input) {
        return Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+").matcher(input).matches();
    }

    private boolean isDui(String input) {
        return Pattern.compile("^\\d{9}$").matcher(input).matches();
    }

    private void handleServerError(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error del servidor: " + response.code() + ", Detalle: " + errorBody);
        } catch (IOException e) {
            Log.e(TAG, "Error leyendo el cuerpo del error", e);
        }

        if (response.code() == 401) {
            Toast.makeText(this, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // ===========================
    // Retrofit interfaces y modelos
    // ===========================

    public interface LoginService {
        @POST("/api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);
    }

    public static class LoginRequest {
        @SerializedName("userEmail")
        private final String email;
        @SerializedName("userDui")
        private final String dui;
        @SerializedName("userPassword")
        private final String password;

        public LoginRequest(String email, String dui, String password) {
            this.email = email;
            this.dui = dui;
            this.password = password;
        }
    }

    public static class LoginResponse {
        private boolean success;
        private String token;

        public boolean isSuccess() {
            return success;
        }

        public String getToken() {
            return token;
        }
    }
}
