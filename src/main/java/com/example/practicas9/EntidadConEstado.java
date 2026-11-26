package com.example.practicas9;

import java.io.IOException;

//Estado de los objetos del mapa
public abstract class EntidadConEstado extends EntidadMapa {

    private boolean estado;

    //Recibe el nombre y posicion del objeto
    protected EntidadConEstado(String nombre, int x, int y) {
        super(nombre, x, y);
    }

    //Las clases hijas consultan y cambian su estado
    protected boolean getEstado() { return estado; }
    protected void setEstado(boolean estado) { this.estado = estado; }

    //Nivel guarda la posicion y el estado del objeto
    @Override
    public String guardarEstado() {
        return super.guardarEstado() + "," + estado;
    }

    //Se guardan X, Y y estado
    @Override
    protected int cantidadCampos() { return 3; }

    //Nivel revisa los datos antes de cargar
    @Override
    public void validarEstado(String[] campos) throws IOException {
        super.validarEstado(campos);
        validarBooleano(campos[2]);
    }

    //Nivel recupera la posicion y el estado guardado
    @Override
    public void restaurarEstado(String[] campos) {
        super.restaurarEstado(campos);
        estado = Boolean.parseBoolean(campos[2]);
    }
}