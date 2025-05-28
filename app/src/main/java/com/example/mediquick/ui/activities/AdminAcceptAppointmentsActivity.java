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
            appointments.clear(); // Limpia lista antes de actualizar

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BACKEND_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            AppointmentService service = retrofit.create(AppointmentService.class);
            Call<AppointmentResponse> call = service.getAllAppointments("1");

            call.enqueue(new Callback<AppointmentResponse>() {
                @Override
                public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        List<com.example.mediquick.data.model.Appointment> fetchedAppointments = response.body().getData();
                        Log.d("CITAS", "Total de citas pendientes: " + fetchedAppointments.size());

                        for (com.example.mediquick.data.model.Appointment appointment : fetchedAppointments) {
                            // Convierte si es necesario, aquí se asume que son del mismo tipo
                            appointments.add(new Appointment(
                                    "1",
                                    appointment.getMedical_appointment_id(),
                                    appointment.getPatient_user_id(),
                                    appointment.getMedical_appointment_date_time()
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);


                        // Aquí podrías agregar las citas a la lista si lo deseas:
                        // appointments.addAll(fetchedAppointments);
                        // adapter.notifyDataSetChanged();
                        // emptyView.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        Log.e("CITAS", "Respuesta no exitosa: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                    Log.e("CITAS", "Error al obtener citas: " + t.getMessage());
                }
            });
        }

    }
    // <!--Moris Navas-->