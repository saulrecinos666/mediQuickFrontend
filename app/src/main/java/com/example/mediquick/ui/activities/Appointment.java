package com.example.mediquick.ui.activities;

public class Appointment {
    private String id;
    private String paciente;
    private String sucursal;
    private String fecha;

    public Appointment(String id, String paciente, String sucursal, String fecha) {
        this.id = id;
        this.paciente = paciente;
        this.sucursal = sucursal;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public String getPaciente() {
        return paciente;
    }

    public String getSucursal() {
        return sucursal;
    }

    public String getFecha() {
        return fecha;
    }
}
//    <!--Moris Navas-->