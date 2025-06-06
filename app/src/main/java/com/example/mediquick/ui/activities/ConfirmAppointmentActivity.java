package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class ConfirmAppointmentActivity extends AppCompatActivity {

    private TextView txtResumen;
    private Button btnConfirmar;
    private ProgressBar progressBar;
    private String procedureId, procedureName, branchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        txtResumen = findViewById(R.id.txtResumenCita);
        btnConfirmar = findViewById(R.id.btnConfirmarCita);
        progressBar = findViewById(R.id.progressBarConfirmar);

        procedureId = getIntent().getStringExtra("procedureId");
        procedureName = getIntent().getStringExtra("procedureName");
        branchId = getIntent().getStringExtra("branchId");

        txtResumen.setText("¿Deseas agendar una cita para: " + procedureName + "?");

        btnConfirmar.setOnClickListener(v -> confirmarCita());
    }

    private void confirmarCita() {
        btnConfirmar.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("medicalProcedureId", procedureId)
                .add("branchId", branchId)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.BACKEND_BASE_URL + "/appointments")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    btnConfirmar.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ConfirmAppointmentActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    btnConfirmar.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                });

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ConfirmAppointmentActivity.this, "✅ Cita creada. Recibirá notificación cuando esté agendada", Toast.LENGTH_LONG).show();
                        finish(); // cerrar pantalla
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ConfirmAppointmentActivity.this, "No se pudo agendar", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
