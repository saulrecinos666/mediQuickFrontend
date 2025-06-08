package com.example.mediquick.data.model;

public class Doctor {
    private String id;
    private String nombreCompleto;

    public Doctor(String id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }

    public String getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    @Override
    public String toString() {
        return nombreCompleto;
    }
}
//    <!--Moris Navas-->