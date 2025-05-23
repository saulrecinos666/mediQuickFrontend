package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;

import java.util.ArrayList;
import java.util.List;

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
        appointments.add(new Appointment("098", "Luis Hernández Díaz", "Clínica Central San Salvador", "19/05/2025"));
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
// <!--Moris Navas-->