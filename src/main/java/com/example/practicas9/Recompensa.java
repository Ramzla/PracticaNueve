package com.example.practicas9;

//Perlas que recoge el buzo
public class Recompensa extends EntidadConEstado implements Inventariable {

    private final String imagen;

    //Nivel crea las perlas con su posicion e imagen
    public Recompensa(String nombre, int x, int y, String imagen) {
        super(nombre, x, y);
        this.imagen = imagen;
    }

    //Interfaz obtiene la imagen de la perla
    public String getImagen() { return imagen; }

    //Nivel e Interfaz revisan si la perla fue recogida
    public boolean isRecogida() { return getEstado(); }

    //Buzo recoge la perla si esta encima y hay espacio
    public boolean recoger(Personaje buzo, Inventario inventario) {
        return !isRecogida() && mismaPosicion(buzo) && inventario.agregarItem(this);
    }

    //Inventario marca la perla como recogida
    @Override public void registrar() {
        setEstado(true);
        System.out.println("Recompensa obtenida: " + getNombre());
    }

    //Inventario retira la perla al recuperar el progreso
    @Override public void borrar() {
        setEstado(false);
        System.out.println("Recompensa retirada: " + getNombre());
    }
}