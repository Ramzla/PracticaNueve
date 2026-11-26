package com.example.practicas9;

//Corales del mapa
public class Obstaculo extends EntidadConEstado implements Destruible {

    //Nombre y posicion del coral
    public Obstaculo(String nombre, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
    }

    //Revisa si el coral esta destruido en un futuro
    public boolean isDestruido() { return getEstado(); }

    //Destruye el coral en un futuro
    @Override
    public void destruye() { setEstado(true); }
}