package com.example.mediquick.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RegisterActivity extends AppCompatActivity {

    // UI Components
    private EditText etFechaNacimiento, etPrimerNombre, etSegundoNombre, etPrimerApellido, etSegundoApellido;
    private EditText etCorreo, etDui, etPass, etTelefono, etDireccion;
    private Button btnContinuar, btnCancelar;
    private RadioButton rbMasculino, rbFemenino;
    private RadioGroup rgGenero;

    // Services
    private RegisterService registerService;

    // Constants
    private static final String TAG = "RegisterActivity";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_AGE_YEARS = 18;
    private static final Pattern DUI_PATTERN = Pattern.compile("^\\d{8}-\\d{1}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");

    // Validation flags (solo para estado interno, no muestran errores)
    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;
    private boolean isDuiValid = false;
    private boolean isPhoneValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupWindowInsets();
        initializeViews();
        setupRetrofit();
        setupListeners();
        setupValidation();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etPrimerNombre = findViewById(R.id.etPrimerNombre);
        etSegundoNombre = findViewById(R.id.etSegundoNombre);
        etPrimerApellido = findViewById(R.id.etPrimerApellido);
        etSegundoApellido = findViewById(R.id.etSegundoApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etDui = findViewById(R.id.etDui);
        etPass = findViewById(R.id.etPass);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btnContinuar = findViewById(R.id.btnContinuar);
        btnCancelar = findViewById(R.id.btnCancelar);
        rbMasculino = findViewById(R.id.rbMasculino);
        rbFemenino = findViewById(R.id.rbFemenino);
        rgGenero = findViewById(R.id.rgGenero);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACKEND_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        registerService = retrofit.create(RegisterService.class);
    }

    private void setupListeners() {
        btnContinuar.setOnClickListener(v -> performRegister());
        btnCancelar.setOnClickListener(v -> navigateToLogin());
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());

        // Setup text formatting for DUI and phone
        setupDuiFormatting();
        setupPhoneFormatting();
    }

    private void setupValidation() {
        // Validación SILENCIOSA en tiempo real (solo actualiza flags internos)
        etCorreo.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailValid = validateEmailSilent(s.toString());
            }
        });

        etPass.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordValid = validatePasswordSilent(s.toString());
            }
        });

        etDui.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isDuiValid = validateDuiSilent(s.toString());
            }
        });

        etTelefono.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPhoneValid = validatePhoneSilent(s.toString());
            }
        });
    }

    private void setupDuiFormatting() {
        etDui.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String text = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < text.length() && i < 9; i++) {
                    if (i == 8) {
                        formatted.append("-");
                    }
                    formatted.append(text.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });
    }

    private void setupPhoneFormatting() {
        etTelefono.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String text = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < text.length() && i < 8; i++) {
                    if (i == 4) {
                        formatted.append("-");
                    }
                    formatted.append(text.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });
    }

    // ===== VALIDACIONES SILENCIOSAS (no muestran Toast) =====
    private boolean validateEmailSilent(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePasswordSilent(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && isValidPassword(password);
    }

    private boolean validateDuiSilent(String dui) {
        return !dui.isEmpty() && DUI_PATTERN.matcher(dui).matches();
    }

    private boolean validatePhoneSilent(String phone) {
        return !phone.isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }

    // ===== VALIDACIONES CON MENSAJES (solo para el submit) =====
    private boolean validateEmailWithMessage(String email) {
        if (email.isEmpty()) {
            showFieldError("El correo electrónico es requerido");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showFieldError("Ingrese un correo electrónico válido");
            return false;
        }
        return true;
    }

    private boolean validatePasswordWithMessage(String password) {
        if (password.isEmpty()) {
            showFieldError("La contraseña es requerida");
            return false;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            showFieldError("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
            return false;
        } else if (!isValidPassword(password)) {
            showFieldError("La contraseña debe incluir mayúsculas, minúsculas y números");
            return false;
        }
        return true;
    }

    private boolean validateDuiWithMessage(String dui) {
        if (dui.isEmpty()) {
            showFieldError("El DUI es requerido");
            return false;
        } else if (!DUI_PATTERN.matcher(dui).matches()) {
            showFieldError("Formato de DUI inválido (12345678-9)");
            return false;
        }
        return true;
    }

    private boolean validatePhoneWithMessage(String phone) {
        if (phone.isEmpty()) {
            showFieldError("El teléfono es requerido");
            return false;
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            showFieldError("Formato de teléfono inválido (7777-7777)");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.matches(".*[A-Z].*") && // At least one uppercase
                password.matches(".*[a-z].*") && // At least one lowercase
                password.matches(".*\\d.*");     // At least one digit
    }

    private boolean validateAge(String birthDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date birth = sdf.parse(birthDate);
            Date now = new Date();

            long ageInMillis = now.getTime() - birth.getTime();
            long ageInYears = TimeUnit.MILLISECONDS.toDays(ageInMillis) / 365;

            return ageInYears >= MIN_AGE_YEARS;
        } catch (Exception e) {
            return false;
        }
    }

    private void showFieldError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void performRegister() {
        // Get values
        String primerNombre = etPrimerNombre.getText().toString().trim();
        String segundoNombre = etSegundoNombre.getText().toString().trim();
        String primerApellido = etPrimerApellido.getText().toString().trim();
        String segundoApellido = etSegundoApellido.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String dui = etDui.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // Validate required fields ONE BY ONE con focus
        if (primerNombre.isEmpty()) {
            showFieldError("El primer nombre es requerido");
            etPrimerNombre.requestFocus();
            return;
        }

        if (primerApellido.isEmpty()) {
            showFieldError("El primer apellido es requerido");
            etPrimerApellido.requestFocus();
            return;
        }

        if (fechaNacimiento.isEmpty()) {
            showFieldError("Seleccione su fecha de nacimiento");
            return;
        }

        if (!validateAge(fechaNacimiento)) {
            showFieldError("Debe ser mayor de " + MIN_AGE_YEARS + " años");
            return;
        }

        if (rgGenero.getCheckedRadioButtonId() == -1) {
            showFieldError("Seleccione su género");
            return;
        }

        if (direccion.isEmpty()) {
            showFieldError("La dirección es requerida");
            etDireccion.requestFocus();
            return;
        }

        // Validate fields with specific messages
        if (!validateEmailWithMessage(correo)) {
            etCorreo.requestFocus();
            return;
        }

        if (!validateDuiWithMessage(dui)) {
            etDui.requestFocus();
            return;
        }

        if (!validatePasswordWithMessage(password)) {
            etPass.requestFocus();
            return;
        }

        if (!validatePhoneWithMessage(telefono)) {
            etTelefono.requestFocus();
            return;
        }

        // Determine gender
        String genero = rbMasculino.isChecked() ? "MALE" : "FEMALE";

        // Format birth date
        String cumple = formatBirthDate(fechaNacimiento);
        if (cumple == null) {
            showFieldError("Error en el formato de fecha");
            return;
        }

        // Disable button during request
        btnContinuar.setEnabled(false);
        btnContinuar.setText("Registrando...");

        // Create request
        RegisterRequest request = new RegisterRequest(
                genero, primerNombre, segundoNombre, "N/A", primerApellido, segundoApellido, "N/A",
                correo, dui, password, cumple, telefono, direccion, new int[]{4}
        );

        // Log request for debugging
        logRequest(request);

        // Make API call
        registerService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                btnContinuar.setEnabled(true);
                btnContinuar.setText("Continuar");

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulRegistration();
                } else {
                    handleRegistrationError(response);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                btnContinuar.setEnabled(true);
                btnContinuar.setText("Continuar");
                handleNetworkError(t);
            }
        });
    }

    private String formatBirthDate(String fechaNacimiento) {
        try {
            String[] fechaParts = fechaNacimiento.split("/");
            if (fechaParts.length == 3) {
                return fechaParts[2] + "-" + String.format("%02d", Integer.parseInt(fechaParts[1])) +
                        "-" + String.format("%02d", Integer.parseInt(fechaParts[0])) + "T00:00:00Z";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting birth date", e);
        }
        return null;
    }

    private void logRequest(RegisterRequest request) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(request);
        Log.d(TAG, "JSON request: " + jsonRequest);
    }

    private void handleSuccessfulRegistration() {
        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void handleRegistrationError(Response<RegisterResponse> response) {
        Log.e(TAG, "Registration failed with code: " + response.code());

        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error body: " + errorBody);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading error body", e);
        }

        String message;
        switch (response.code()) {
            case 400:
                message = "Datos inválidos. Verifique la información";
                break;
            case 409:
                message = "El usuario ya existe";
                break;
            case 500:
                message = "Error del servidor. Intente más tarde";
                break;
            default:
                message = "Error de registro. Código: " + response.code();
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error", t);
        Toast.makeText(this, "Error de conexión. Verifique su internet", Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etFechaNacimiento.setText(fecha);
                },
                year, month, day
        );

        // Set max date to 18 years ago
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -MIN_AGE_YEARS);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        // Set min date to 100 years ago
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    // Abstract TextWatcher class to reduce boilerplate
    private abstract class ValidationTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    // API Interface
    public interface RegisterService {
        @POST("/api/auth/register")
        Call<RegisterResponse> register(@Body RegisterRequest request);
    }

    // Request/Response classes
    public static class RegisterRequest {
        @SerializedName("userGender")
        private final String genero;
        @SerializedName("userFirstName")
        private final String primerNombre;
        @SerializedName("userSecondName")
        private final String segundoNombre;
        @SerializedName("userThirdName")
        private final String tercerNombre;
        @SerializedName("userFirstLastname")
        private final String primerApellido;
        @SerializedName("userSecondLastname")
        private final String segundoApellido;
        @SerializedName("userThirdLastname")
        private final String tercerApellido;
        @SerializedName("userEmail")
        private final String correo;
        @SerializedName("userDui")
        private final String dui;
        @SerializedName("userPassword")
        private final String pass;
        @SerializedName("userBirthdate")
        private final String cumple;
        @SerializedName("userPhoneNumber")
        private final String telefono;
        @SerializedName("userAddress")
        private final String direccion;
        @SerializedName("profilesId")
        private final int[] perfiles;

        public RegisterRequest(String genero, String primerNombre, String segundoNombre, String tercerNombre,
                               String primerApellido, String segundoApellido, String tercerApellido, String correo,
                               String dui, String pass, String cumple, String telefono, String direccion, int[] perfiles) {
            this.genero = genero;
            this.primerNombre = primerNombre;
            this.segundoNombre = segundoNombre;
            this.tercerNombre = tercerNombre;
            this.primerApellido = primerApellido;
            this.segundoApellido = segundoApellido;
            this.tercerApellido = tercerApellido;
            this.correo = correo;
            this.dui = dui;
            this.pass = pass;
            this.cumple = cumple;
            this.telefono = telefono;
            this.direccion = direccion;
            this.perfiles = perfiles;
        }

        // Getters
        public String getGenero() { return genero; }
        public String getPrimerNombre() { return primerNombre; }
        public String getSegundoNombre() { return segundoNombre; }
        public String getTercerNombre() { return tercerNombre; }
        public String getPrimerApellido() { return primerApellido; }
        public String getSegundoApellido() { return segundoApellido; }
        public String getTercerApellido() { return tercerApellido; }
        public String getCorreo() { return correo; }
        public String getDui() { return dui; }
        public String getPass() { return pass; }
        public String getCumple() { return cumple; }
        public String getTelefono() { return telefono; }
        public String getDireccion() { return direccion; }
        public int[] getPerfiles() { return perfiles; }
    }

    public static class RegisterResponse {
        @SerializedName("success")
        private boolean success;

        @SerializedName("message")
        private String message;

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}