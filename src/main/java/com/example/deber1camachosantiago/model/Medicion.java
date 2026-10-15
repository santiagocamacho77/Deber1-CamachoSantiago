package com.example.deber1camachosantiago.model;

// Guarda el resultado de una carga: si fue con o sin cache y cuanto se demoro
public class Medicion {

    private int numero;
    private String tipo;
    private int registros;
    private double tiempoMs;
    private String hora;

    public Medicion(int numero, String tipo, int registros, double tiempoMs, String hora) {
        this.numero = numero;
        this.tipo = tipo;
        this.registros = registros;
        this.tiempoMs = tiempoMs;
        this.hora = hora;
    }

    public int getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public int getRegistros() {
        return registros;
    }

    public double getTiempoMs() {
        return tiempoMs;
    }

    public String getHora() {
        return hora;
    }
}
