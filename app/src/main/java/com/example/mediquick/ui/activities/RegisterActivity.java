package com.example.mediquick.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFechaNacimiento, etPrimerNombre, etSegundoNombre, etPrimerApellido, etSegundoApellido, etCorreo, etDui, etPass, etTelefono, etDireccion;
    private Button btnContinuar, btnCancelar;
    private RegisterService registerService;
    private String genero, primerNombre, segundoNombre, tercerNombre, primerApellido, segundoApellido, tercerApellido, correo, dui, pass, cumple,
            telefono, direccion;
    private int[] perfiles;

    private RadioButton rbMasculino, rbFemenino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnContinuar = findViewById(R.id.btnContinuar);
        etPrimerNombre = findViewById(R.id.etPrimerNombre);
        etSegundoNombre = findViewById(R.id.etSegundoNombre);
        etPrimerApellido = findViewById(R.id.etPrimerApellido);
        etSegundoApellido = findViewById(R.id.etSegundoApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etDui = findViewById(R.id.etDui);
        etPass = findViewById(R.id.etPass);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        rbMasculino = findViewById(R.id.rbMasculino);
        rbFemenino = findViewById(R.id.rbFemenino);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACKEND_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        registerService = retrofit.create(RegisterService.class);

        btnContinuar.setOnClickListener(v -> performRegister());

        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        btnCancelar.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private  void performRegister(){
        String primerNombre = etPrimerNombre.getText().toString().trim();
        String segundoNombre = etSegundoNombre.getText().toString().trim();
        String tercerNombre = "a";
        String primerApellido = etPrimerApellido.getText().toString().trim();
        String segundoApellido = etSegundoApellido.getText().toString().trim();
        String tercerApellido = "a";
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String dui = etDui.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        if (rbMasculino.isChecked()) {
            genero = "MALE";
        } else if (rbFemenino.isChecked()) {
            genero = "FEMALE";
        } else {
            Toast.makeText(this, "Seleccione su género.", Toast.LENGTH_LONG).show();
            return;
        }

        if(primerNombre.isEmpty() | segundoNombre.isEmpty() | primerApellido.isEmpty() | segundoApellido.isEmpty() |
        fechaNacimiento.isEmpty() | correo.isEmpty() | dui.isEmpty() | pass.isEmpty() | telefono.isEmpty() |
        direccion.isEmpty()){
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] fechaParts = fechaNacimiento.split("/");
        if (fechaParts.length == 3) {
            cumple = fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0] + "T00:00:00Z";
        } else {
            Toast.makeText(this, "Formato de fecha incorrecto.", Toast.LENGTH_LONG).show();
            return;
        }

        perfiles = new int[]{4};

        RegisterRequest request = new RegisterRequest(
                genero, primerNombre, segundoNombre, tercerNombre, primerApellido, segundoApellido, tercerApellido, correo,
                dui, pass, cumple, telefono, direccion, perfiles);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(request);
        Log.d("RegisterActivity", "JSON que se enviará: " + jsonRequest);

        registerService.register(new RegisterRequest(genero, primerNombre, segundoNombre, tercerNombre, primerApellido, segundoApellido, tercerApellido, correo,
                dui, pass, cumple, telefono, direccion, perfiles)).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registro guardado con exito.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("RegisterActivity", "Error del servidor: " + response.code());
                    try {
                        Log.e("RegisterActivity", "Cuerpo del error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("RegisterActivity", "Error al leer el cuerpo del error", e);
                    }
                    if (response.code() == 401) {
                        Toast.makeText(RegisterActivity.this, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RegisterActivity", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        etFechaNacimiento.setText(fecha);
                    }
                }, anio, mes, dia);

        datePickerDialog.show();
    }

    public interface RegisterService {
        @POST("/api/auth/register")
        Call<RegisterActivity.RegisterResponse> register(@Body RegisterActivity.RegisterRequest request);
    }

    public static class RegisterRequest {
        @SerializedName("userGender")
        private String genero;
        @SerializedName("userFirstName")
        private String primerNombre;
        @SerializedName("userSecondName")
        private String segundoNombre;
        @SerializedName("userThirdName")
        private String tercerNombre;
        @SerializedName("userFirstLastname")
        private String primerApellido;
        @SerializedName("userSecondLastname")
        private String segundoApellido;
        @SerializedName("userThirdLastname")
        private String tercerApellido;
        @SerializedName("userEmail")
        private String correo;
        @SerializedName("userDui")
        private String dui;
        @SerializedName("userPassword")
        private String pass;
        @SerializedName("userBirthdate")
        private String cumple;
        @SerializedName("userPhoneNumber")
        private String telefono;
        @SerializedName("userAddress")
        private String direccion;
        @SerializedName("profilesId")
        private int[] perfiles;

        public RegisterRequest(String genero, String primerNombre, String segundoNombre, String tercerNombre, String primerApellido,
                               String segundoApellido, String tercerApellido, String correo, String dui, String pass,
                               String cumple, String telefono, String direccion, int[] perfiles)
        {
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

        public String getGenero() {
            return genero;
        }
        public String getPrimerNombre() {
            return primerNombre;
        }
        public String getSegundoNombre() {
            return segundoNombre;
        }
        public String getTercerNombre() {return tercerNombre;}
        public String getPrimerApellido() {return primerApellido; }
        public String getSegundoApellido() {
            return segundoApellido;
        }
        public String getTercerApellido() {return tercerApellido;}
        public String getCorreo() {
            return correo;
        }
        public String getDui() {
            return dui;
        }
        public String getPass() {
            return pass;
        }
        public String getCumple() {
            return cumple;
        }
        public String getTelefono() {
            return telefono;
        }
        public String getDireccion() {
            return direccion;
        }
        public int[] getPerfiles() { return perfiles; }
    }

    public static class RegisterResponse {
        private boolean success;
        public boolean isSuccess() {
            return success;
        }
    }
}
