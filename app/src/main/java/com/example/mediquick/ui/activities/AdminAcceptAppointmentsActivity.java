package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentResponse;
import com.example.mediquick.ui.activities.Appointment;
import com.example.mediquick.services.AppointmentService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminAcceptAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerAppointments;
    private TextView emptyView;
    private List<Appointment> appointments = new ArrayList<>();
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accept_appointments);

        recyclerAppointments = findViewById(R.id.recyclerAppointments);
        emptyView = findViewById(R.id.emptyView);

        adapter = new AppointmentAdapter(appointments, this, appointment -> {
            AssignAppointmentDialog dialog = new AssignAppointmentDialog(this, appointment);
            dialog.show();
        });

        recyclerAppointments.setAdapter(adapter);
        recyclerAppointments.setLayoutManager(new LinearLayoutManager(this));

        loadAppointments();
    }

    private void loadAppointments() {
        appointments.clear();

        // ✅ Agregar logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACKEND_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AppointmentService service = retrofit.create(AppointmentService.class);
        Call<AppointmentResponse> call = service.getAllAppointments("1");

        // ✅ Logs para debugging
        Log.d("CITAS", "URL Base: " + BuildConfig.BACKEND_BASE_URL);
        Log.d("CITAS", "Iniciando petición...");
        Log.d("CITAS", "URL completa: " + call.request().url().toString());
        call.enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                Log.d("CITAS", "Respuesta recibida - Código: " + response.code());
                Log.d("CITAS", "URL completa: " + call.request().url().toString());

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ AQUÍ ESTÁ EL CÓDIGO QUE FALTABA PARA PINTAR LAS CITAS
                    List<com.example.mediquick.data.model.Appointment> fetchedAppointments = response.body().getData();
                    Log.d("CITAS", "Total de citas pendientes: " + fetchedAppointments.size());

                    // ✅ LOG DETALLADO DE CADA CITA OBTENIDA
                    for (int i = 0; i < fetchedAppointments.size(); i++) {
                        com.example.mediquick.data.model.Appointment appointment = fetchedAppointments.get(i);
                        Log.d("CITAS", "Cita " + (i + 1) + ":");
                        Log.d("CITAS", "  - ID: " + appointment.getMedical_appointment_id());
                        Log.d("CITAS", "  - Patient ID: " + appointment.getPatient_user_id());
                        Log.d("CITAS", "  - Fecha/Hora: " + appointment.getMedical_appointment_date_time());
                        //Log.d("CITAS", "  - Estado: " + (appointment.getsta != null ? appointment.getStatus() : "N/A"));
                        Log.d("CITAS", "  - Doctor ID: " + (appointment.getDoctor_user_id() != null ? appointment.getDoctor_user_id() : "N/A"));
                        Log.d("CITAS", "---");
                    }

                    // Limpiar lista anterior
                    appointments.clear();

                    // Agregar las citas obtenidas del servidor
                    for (com.example.mediquick.data.model.Appointment appointment : fetchedAppointments) {

                        String appointmentDate = appointment.getMedical_appointment_date_time() != null &&
                                !appointment.getMedical_appointment_date_time().trim().isEmpty() ?
                                appointment.getMedical_appointment_date_time() : "Sin fecha asignada";

                        Log.d("CITAS", "Agregando cita con ID: " + appointment.getMedical_appointment_id());
                        appointments.add(new Appointment(
                                appointment.getMedical_appointment_id(),
                                appointment.getPatient_user().getFullName(),
                                appointment.getBranch().getBranch_name(),
                                appointmentDate
                        ));
                    }

                    Log.d("CITAS", "Total de citas agregadas a la lista local: " + appointments.size());

                    // ✅ NOTIFICAR AL ADAPTER QUE LOS DATOS CAMBIARON
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();

                        // ✅ MOSTRAR/OCULTAR VISTA VACÍA
                        if (appointments.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerAppointments.setVisibility(View.GONE);
                            Log.d("CITAS", "No hay citas para mostrar");
                        } else {
                            emptyView.setVisibility(View.GONE);
                            recyclerAppointments.setVisibility(View.VISIBLE);
                            Log.d("CITAS", "Mostrando " + appointments.size() + " citas");
                        }
                    });

                } else {
                    Log.e("CITAS", "Respuesta no exitosa: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                        Log.e("CITAS", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("CITAS", "No se pudo leer error body");
                    }

                    // ✅ MOSTRAR VISTA VACÍA EN CASO DE ERROR
                    runOnUiThread(() -> {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerAppointments.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Log.e("CITAS", "Error en la petición: " + t.getMessage());
                Log.e("CITAS", "URL que falló: " + call.request().url().toString());
                t.printStackTrace();

                // ✅ MOSTRAR VISTA VACÍA EN CASO DE FALLO
                runOnUiThread(() -> {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerAppointments.setVisibility(View.GONE);
                });
            }
        });
    }

    // ✅ MÉTODO PÚBLICO PARA REFRESCAR DATOS
    public void refreshAppointments() {
        loadAppointments();
    }
}