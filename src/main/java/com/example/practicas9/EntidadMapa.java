package com.example.practicas9;

import java.util.Objects;
import java.io.IOException;

//Clase base de los objetos del mapa
public abstract class EntidadMapa {

    private final String nombre;
    private int posicionX;
    private int posicionY;

    //Recibe el nombre y posicion de cada objeto
    protected EntidadMapa(String nombre, int posicionX, int posicionY) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    //Interfaz usa las posiciones para dibujar los objetos
    public String getNombre() { return nombre; }
    public int getPosicionX() { return posicionX; }
    public int getPosicionY() { return posicionY; }

    //Cambia la posicion al mover o cargar un objeto
    public void setPosicion(int x, int y) {
        posicionX = x;
        posicionY = y;
    }

    //Nivel revisa si hay un coral en esa posicion
    public boolean mismaPosicion(int x, int y) {
        return posicionX == x && posicionY == y;
    }

    //Tiburon y Recompensa revisan si coinciden con el buzo
    public boolean mismaPosicion(EntidadMapa otro) {
        return mismaPosicion(otro.getPosicionX(), otro.getPosicionY());
    }

    //Arma y Utileria revisan la distancia al buzo
    public int distancia(EntidadMapa otro) {
        return Math.abs(posicionX - otro.getPosicionX())
                + Math.abs(posicionY - otro.getPosicionY());
    }

    //Personaje y Tiburon calculan a donde pueden avanzar
    public int[] siguienteLugar(String direccion) {
        return calcularPosicion(direccion, posicionX, posicionY, 1);
    }

    //Calcula la nueva posicion segun la direccion
    private int[] calcularPosicion(String direccion, int x, int y, int distancia) {
        if (distancia < 0 || direccion == null)
            throw new IllegalArgumentException("Movimiento inválido");

        return switch (direccion.toLowerCase(java.util.Locale.ROOT)) {
            case "norte" -> new int[]{x, y - distancia};
            case "sur" -> new int[]{x, y + distancia};
            case "este" -> new int[]{x + distancia, y};
            case "oeste" -> new int[]{x - distancia, y};
            default -> throw new IllegalArgumentException("Dirección inválida: " + direccion);
        };
    }

    //Personaje y Utileria usan este metodo para moverse
    protected final void desplazar(String direccion, int distancia) {
        int[] nueva = calcularPosicion(direccion, posicionX, posicionY, distancia);
        setPosicion(nueva[0], nueva[1]);
    }

    //Nivel guarda las coordenadas de los objetos
    public String guardarEstado() {
        return posicionX + "," + posicionY;
    }

    //Cantidad de datos que se guardan (posiciones)
    protected int cantidadCampos() { return 2; }

    //Nivel revisa los datos antes de cargar
    public void validarEstado(String[] campos) throws IOException {
        if (campos.length != cantidadCampos())
            throw new IOException("Formato inválido: " + nombre);

        Integer.parseInt(campos[0]);
        Integer.parseInt(campos[1]);
    }

    //EntidadConEstado revisa que el estado sea valido
    protected void validarBooleano(String valor) throws IOException {
        if (!valor.equals("true") && !valor.equals("false"))
            throw new IOException("Estado inválido: " + nombre);
    }

    //Nivel recupera las posiciones guardadas
    public void restaurarEstado(String[] campos) {
        setPosicion(Integer.parseInt(campos[0]), Integer.parseInt(campos[1]));
    }
}