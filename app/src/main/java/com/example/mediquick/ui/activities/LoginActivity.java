package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrDui, etPassword;
    private Button btnLogin, btnRegister;
    private LoginService loginService;
    private String email, dui;
    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";
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

        etEmailOrDui = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACKEND_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loginService = retrofit.create(LoginService.class);

        btnLogin.setOnClickListener(v -> performLogin());

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        String storedToken = retrieveAuthToken();
        if(storedToken != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void performLogin() {
        String emailOrDui = etEmailOrDui.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (emailOrDui.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidEmailOrDui(emailOrDui)) {
            email = emailOrDui;
            dui = "";
        } else {
            email = "";
            dui = emailOrDui;
        }

        loginService.login(new LoginRequest(email, dui, pass)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();

                    String token = response.body().getToken();
                    saveAuthToken(token);

                    /*Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();*/

                    Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Error del servidor: " + response.code());
                    try {
                        Log.e("LoginActivity", "Cuerpo del error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("LoginActivity", "Error al leer el cuerpo del error", e);
                    }
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Error: " + t.getMessage(), t);
            }
        });
    }

    private boolean isValidEmailOrDui(String input) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String duiPattern = "^\\d{9}$";

        Pattern emailPatternObj = Pattern.compile(emailPattern);
        Pattern duiPatternObj = Pattern.compile(duiPattern);

        Matcher emailMatcher = emailPatternObj.matcher(input);
        Matcher duiMatcher = duiPatternObj.matcher(input);

        return emailMatcher.matches() || duiMatcher.matches();
    }

    private void saveAuthToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
        Log.d(TAG, "Token guardado: " + token);
    }

    private String retrieveAuthToken() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
    }

    public interface LoginService {
        @POST("/api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);
    }

    public static class LoginRequest {
        @SerializedName("userEmail")
        private String email;
        @SerializedName("userDui")
        private String dui;
        @SerializedName("userPassword")
        private String password;

        public LoginRequest(String email, String dui, String password) {
            this.email = email;
            this.dui = dui;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getDui() {
            return dui;
        }

        public String getPassword() {
            return password;
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

