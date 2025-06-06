package com.example.mediquick.data.model;

public class PrescriptionForm {
    private String nombre = "", dosis = "", unidad = "", frecuencia = "", duracion = "", notas = "";

    public String getNombre() { return nombre; }
    public String getDosis() { return dosis; }
    public String getUnidad() { return unidad; }
    public String getFrecuencia() { return frecuencia; }
    public String getDuracion() { return duracion; }
    public String getNotas() { return notas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public void setNotas(String notas) { this.notas = notas; }
}
