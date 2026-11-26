package com.example.practicas9;

import java.util.HashMap;
import java.util.Map;

//Registro de los objetos del mapa
public class EntidadPersonaje {

    //Guarda los objetos con su identificacion
    private final Map<String, EntidadMapa> entidades = new HashMap<>();

    //Registra un objeto
    public void registrar(String identificacion, EntidadMapa objeto) {
        entidades.put(identificacion, objeto);
    }

    //Busca un objeto por su identificacion
    public EntidadMapa buscar(String identificacion) {
        EntidadMapa objeto = entidades.get(identificacion);

        //Si no existe el objeto marca un error
        if (objeto == null)
            throw new IllegalArgumentException("No existe: " + identificacion);

        return objeto;
    }

    //Obtiene los objetos registrados
    public Buzo getBuzo() { return (Buzo) buscar("buzo"); }
    public Utileria getBotiquin() { return (Utileria) buscar("botiquin"); }
    public CheckPoint getBoya() { return (CheckPoint) buscar("boya"); }
    public Tiburon getTiburon(int numero) { return (Tiburon) buscar("tiburon" + numero); }
}