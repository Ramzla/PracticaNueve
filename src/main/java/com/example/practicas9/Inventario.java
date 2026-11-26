package com.example.practicas9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Administra las pertenencias del buzo
public class Inventario {

    private final int capacidadMaxima;
    private final List<Inventariable> items = new ArrayList<>();

    //Buzo define la capacidad del inventario
    public Inventario(int capacidadMaxima) {
        if (capacidadMaxima < 0) throw new IllegalArgumentException("Capacidad inválida");
        this.capacidadMaxima = capacidadMaxima;
    }

    //Nivel revisa el espacio al cargar la partida
    public int getCapacidadMaxima() { return capacidadMaxima; }

    //Nivel consulta los objetos sin modificar la lista
    public List<Inventariable> getItems() { return Collections.unmodifiableList(items); }

    //Buzo agrega el arma y Recompensa agrega las perlas
    public boolean agregarItem(Inventariable item) {

        //Revisa si hay espacio y si el objeto no esta repetido
        if (item == null || items.size() >= capacidadMaxima || items.contains(item)) return false;

        items.add(item);
        item.registrar();
        return true;
    }

    //Nivel retira las perlas antes de cargar la partida
    public boolean eliminarItem(Inventariable item) {
        if (!items.remove(item)) return false;
        item.borrar();
        return true;
    }
}