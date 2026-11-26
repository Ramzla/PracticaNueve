package com.example.practicas9;

// Ejecutar con -ea para comprobar que los tiburones siguen activos.
public class PruebaVictoria {
    public static void main(String[] args) {
        Main juego = new Main();
        juego.iniciarJuego();
        Personaje buzo = juego.getJugador();
        buzo.setPosicion(10, 9);
        juego.moverJugador("sur");
        assert !juego.isTermino();
        assert juego.getMensaje().contains("Faltan perlas");
        for (Recompensa perla : juego.getPerlas()) {
            buzo.setPosicion(perla.getPosicionX(), perla.getPosicionY());
            assert perla.recoger(buzo, juego.getNivel().getInventario());
        }
        Tiburon tiburon = juego.getTiburones()[0];
        tiburon.setPosicion(buzo.getPosicionX(), buzo.getPosicionY());
        juego.moverTiburones();
        assert buzo.getVida() == buzo.getVidaMaxima() - 1;
        tiburon.setPosicion(buzo.getPosicionX(), buzo.getPosicionY());
        juego.atacar();
        assert tiburon.getVida() == 25;
        buzo.setPosicion(10, 9);
        juego.moverJugador("sur");
        assert juego.isTermino();
        assert juego.getMensaje().contains("Ganaste");
        System.out.println("Victoria y enemigos activos correctos.");
    }
}
