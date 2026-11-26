package com.example.practicas9;

//Arma que utiliza el buzo
public class Arma implements Inventariable {

    private final String nombre;
    private final int daño;
    private final double alcance;

    //Buzo define el nombre, daño y alcance del arma
    public Arma(String nombre, int daño, double alcance) {
        this.nombre = nombre;
        this.daño = daño;
        this.alcance = alcance;
    }

    //Tiburon consulta el daño que recibe
    public int getDaño() { return daño; }

    // ATACAR_TIBURON - Buzo usa este metodo para atacar
    public String atacar(Personaje buzo, Tiburon[] tiburones) {
        Tiburon objetivo = null;
        int menorDistancia = Integer.MAX_VALUE;

        //Busca al tiburon mas cercano
        for (Tiburon tiburon : tiburones) {
            int d = buzo.distancia(tiburon);

            //Revisa si esta vivo y dentro del alcance
            if (tiburon.getVida() > 0 && d <= alcance && d < menorDistancia) {
                objetivo = tiburon;
                menorDistancia = d;
            }
        }

        //Si no encuentra un tiburon cercano
        if (objetivo == null)
            return "Para atacar, acércate a cualquiera de los tiburones.";

        //El tiburon recibe el daño del arma
        return objetivo.recibirAtaque(this);
    }

    //Inventario registra el arma
    @Override
    public void registrar() {
        System.out.println("Arma equipada: " + nombre);
    }

    //Inventario retira el arma
    @Override
    public void borrar() {
        System.out.println("Arma retirada: " + nombre);
    }

    //Datos del arma como texto
    @Override
    public String toString() {
        return nombre + " [daño " + daño + ", alcance " + alcance + "]";
    }
}