package com.example.practicas9;

import java.util.Objects;

public abstract class EntidadMapa {
    private final String nombre;
    private int posicionX;
    private int posicionY;

    protected EntidadMapa(String nombre, int posicionX, int posicionY) {
        this.nombre = Objects.requireNonNull(nombre, "nombre"); //Evita nombres nulos
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    //consultamos nuestros atributos privados
    public String getNombre() { return nombre; }
    public int getPosicionX() { return posicionX; }
    public int getPosicionY() { return posicionY; }

    //cambianos la posicion de un objeto
    public void setPosicion(int x, int y) {
        posicionX = x;
        posicionY = y;
    }

    //recibira un simbolo de parte de las clases hijas
    public abstract String getSimbolo();

    protected final void desplazar(String direccion, int distancia) {
        if (distancia < 0) throw new IllegalArgumentException("La distancia debe ser >= 0");
        if (direccion == null) throw new IllegalArgumentException("La dirección es obligatoria");
        switch (direccion.toLowerCase(java.util.Locale.ROOT)) {
            case "norte" -> posicionY -= distancia;
            case "sur"   -> posicionY += distancia;
            case "este"  -> posicionX += distancia;
            case "oeste" -> posicionX -= distancia;
            default -> throw new IllegalArgumentException("Dirección inválida: " + direccion);
        }
    }
}
