package com.example.practicas9;

// Ejecutar con -ea para activar las comprobaciones assert.
// Usar una carpeta de pruebas: crea partida.txt.
public class PruebaTiburones {
    public static void main(String[] args) throws Exception {
        Main juego = new Main();
        juego.iniciarJuego();
        Tiburon[] tiburones = juego.getTiburones();
        Personaje buzo = juego.getJugador();
        assert tiburones.length == 6;
        assert tiburones[0].getVida() == 50;
        assert tiburones[0].getPosicionX() == 8;
        assert juego.getNivel().getEntidades().buscar("tiburon1") == tiburones[0];

        // Revisamos las colisiones y bordes durante varios turnos.
        buzo.setPosicion(0, 0);
        for (int turno = 0; turno < 100; turno++) {
            juego.moverTiburones();
            for (int i = 0; i < tiburones.length; i++) {
                Tiburon t = tiburones[i];
                assert juego.getNivel().lugarLibre(t.getPosicionX(), t.getPosicionY());
                for (int j = i + 1; j < tiburones.length; j++) {
                    assert t.distancia(tiburones[j]) > 0;
                }
            }
        }

        // Si dos tiburones alcanzan al buzo, solo se descuenta una vida.
        buzo.setPosicion(1, 1);
        tiburones[0].setPosicion(1, 1);
        tiburones[1].setPosicion(1, 1);
        juego.moverTiburones();
        assert buzo.getVida() == 2;

        // Dos ataques de 25 puntos eliminan al tiburon mas cercano.
        juego.iniciarJuego();
        tiburones = juego.getTiburones();
        buzo = juego.getJugador();
        tiburones[0].setPosicion(1, 2);
        juego.atacar();
        assert tiburones[0].getVida() == 25;
        juego.atacar();
        assert tiburones[0].getVida() == 0;
        juego.moverTiburones();
        assert tiburones[0].getPosicionY() == 2;
        assert !tiburones[0].morder(buzo);

        // Comprobamos que la vida de los tiburones se guarde y recupere.
        tiburones[1].recibirDaño(25);
        juego.getNivel().guardar();
        juego.iniciarJuego();
        assert juego.cargarPartida();
        assert juego.getTiburones()[0].getVida() == 0;
        assert juego.getTiburones()[1].getVida() == 25;
        assert juego.getTiburones()[0].getPosicionY() == 2;
        System.out.println("Pruebas de tiburones y guardado correctas.");
    }
}
