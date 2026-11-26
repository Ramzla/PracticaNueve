package com.example.practicas9;

//Botiquin del mapa
public class Utileria extends EntidadConEstado implements ElementoDinamico {

    //Cantidad de vidas que recupera
    private final int vidasRecuperadas = 1;

    //Nivel crea el botiquin
    public Utileria(String nombre, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
    }

    //Interfaz revisa si ya se uso
    public boolean isUsada() { return getEstado(); }

    //Marca el botiquin como usado
    public void usar() { setEstado(true); }

    // Buzo llama este metodo para recuperar vida
    public String curar(Personaje buzo) {

        //Revisa la distancia y si ya se uso
        if (isUsada() || distancia(buzo) > 1)
            return "Acércate al botiquín verde. Solo puedes usarlo una vez.";

        //Revisa si tiene las vidas completas
        if (buzo.getVida() == buzo.getVidaMaxima())
            return "Ya tienes las " + buzo.getVidaMaxima() + " vidas completas.";

        //Recupera vida y marca el botiquin como usado
        int anterior = buzo.getVida();
        buzo.reparar(vidasRecuperadas);
        usar();

        //Calcula las vidas que recupero
        int recuperadas = buzo.getVida() - anterior;

        return (recuperadas == 1 ? "Recuperaste una vida." : "Recuperaste " + recuperadas + " vidas.")
                + " Vidas: " + buzo.getVida() + "/" + buzo.getVidaMaxima();
    }

    //Movimiento de ElementoDinamico
    @Override
    public void mover(String direccion, int distancia) {
        desplazar(direccion, distancia);
    }
}