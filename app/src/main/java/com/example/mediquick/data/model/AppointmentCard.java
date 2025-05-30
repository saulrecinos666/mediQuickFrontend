package com.example.mediquick.data.model;

public class AppointmentCard {
    private String id, paciente, fecha, sucursal, estado;

    public AppointmentCard(String id, String paciente, String fecha, String sucursal, String estado) {
        this.id = id;
        this.paciente = paciente;
        this.fecha = fecha;
        this.sucursal = sucursal;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getPaciente() { return paciente; }
    public String getFecha() { return fecha; }
    public String getSucursal() { return sucursal; }
    public String getEstado() { return estado; }
}
