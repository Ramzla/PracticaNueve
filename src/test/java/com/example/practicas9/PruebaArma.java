package com.example.practicas9;

// Ejecutar con -ea para comprobar la seleccion del arpon y las dimensiones.
public class PruebaArma {
    public static void main(String[] args) {
        Personaje buzo = new Buzo(3, 1, 1);
        Arma arma = new Arma("Arpón", 25, 2);
        Tiburon lejos = new Tiburon("Lejos", 3, 1);
        Tiburon cerca = new Tiburon("Cerca", 2, 1);
        Tiburon fuera = new Tiburon("Fuera", 4, 1);
        Tiburon muerto = new Tiburon("Muerto", 1, 1);
        muerto.recibirDaño(50);
        Tiburon[] tiburones = {lejos, cerca, fuera, muerto};
        assert arma.atacar(buzo, tiburones).contains("Cerca");
        assert cerca.getVida() == 25 && lejos.getVida() == 50;
        assert arma.atacar(buzo, tiburones).contains("derrotado");
        assert cerca.getVida() == 0;
        assert arma.atacar(buzo, tiburones).contains("Lejos");
        arma.atacar(buzo, tiburones);
        assert arma.atacar(buzo, tiburones).contains("acércate");
        assert fuera.getVida() == 50 && muerto.getVida() == 0;
        Main juego = new Main();
        juego.iniciarJuego();
        Nivel nivel = juego.getNivel();
        assert nivel.getColumnas() == 12 && nivel.getFilas() == 12;
        assert nivel.dentroMapa(10, 10) && !nivel.dentroMapa(11, 10);
        assert !nivel.dentroMapa(10, 11) && !nivel.dentroMapa(0, 1);
        for (Recompensa perla : nivel.getPerlas()) {
            juego.getJugador().setPosicion(perla.getPosicionX(), perla.getPosicionY());
            assert perla.recoger(juego.getJugador(), nivel.getInventario());
        }
        assert nivel.getCantidadPerlas() == nivel.getTotalPerlas();
        Tiburon t = juego.getTiburones()[0];
        t.setPosicion(juego.getJugador().getPosicionX(), juego.getJugador().getPosicionY());
        juego.atacar();
        assert t.getVida() == 25;
        System.out.println("Pruebas del arma y dimensiones correctas.");
    }
}
