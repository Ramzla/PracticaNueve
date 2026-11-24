package com.example.practicas9;

public class Jefe extends EntidadMapa implements ElementoDinamico {
    public static final int VELOCIDAD_MS = 250;

    public Jefe(int x, int y) {
        super("Tiburón gigante", x, y);
    }

    @Override
    public void mover(String direccion, int distancia) {
        desplazar(direccion, distancia);
    }

    @Override
    public String getSimbolo() {
        return "J";
    }
}
