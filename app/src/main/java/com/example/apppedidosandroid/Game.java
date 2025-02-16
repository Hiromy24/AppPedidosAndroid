package com.example.apppedidosandroid;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Game {
    private String Nombre;
    private String Descripcion;
    private List<String> Imagenes;

    @SerializedName("Puntuación")
    private double Puntuacion;

    // Getters and Setters
    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }

    public List<String> getImagenes() {
        return Imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.Imagenes = imagenes;
    }

    public double getPuntuacion() {
        return Puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.Puntuacion = puntuacion;
    }
}