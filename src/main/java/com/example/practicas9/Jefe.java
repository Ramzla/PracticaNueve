package com.example.practicas9;

public class Jefe extends Personaje {
    public static final int VIDA_MAXIMA = 150;
    public static final int VELOCIDAD_MS = 250;

    public Jefe(int x, int y) {
        super("Tiburón gigante", VIDA_MAXIMA, x, y);
    }

    @Override
    public String getSimbolo() { return "J"; }
}
