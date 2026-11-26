package com.example.practicas9;

//Boya de guardado
public class CheckPoint extends EntidadConEstado {

    //Nombre y posicion de la boya
    public CheckPoint(String nombre, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
    }

    public boolean isActivado() { return getEstado(); }
    public void activar() { setEstado(true); }
    public void desactivar() { setEstado(false); }
}
