package com.example.practicas9;

//Jugador del mapa
public class Buzo extends Personaje {

    //Espacio del inventario
    private final Inventario inventario = new Inventario(5);

    //Nombre, daño y alcance del arma
    private final Arma arma = new Arma("Arpón", 25, 1);

    //Nivel crea al buzo con sus vidas y posicion
    public Buzo(int vida, int x, int y) {
        super("Buzo", vida, x, y);
        inventario.agregarItem(arma);
    }

    //Nivel consulta el inventario al cargar
    public Inventario getInventario() { return inventario; }

    //Main llama este metodo al atacar
    public String atacar(Tiburon[] tiburones) {
        return arma.atacar(this, tiburones);
    }

    //Main llama este metodo al usar el botiquin
    public String usarBotiquin(Utileria botiquin) {
        return botiquin.curar(this);
    }

    //Main revisa si el buzo encontro una perla
    public String recogerPerlas(Recompensa[] perlas) {
        String mensaje = null;

        for (Recompensa perla : perlas) {
            if (perla.recoger(this, inventario)) {
                mensaje = "Recogiste " + perla.getNombre() + ".";
            }
        }
        return mensaje;
    }
}