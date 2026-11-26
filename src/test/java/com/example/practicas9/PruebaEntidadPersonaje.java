package com.example.practicas9;

// Ejecutar con -ea en una carpeta de pruebas (crea partida.txt).
public class PruebaEntidadPersonaje {
    public static void main(String[] args) {
        Main juego = new Main();
        juego.iniciarJuego();
        EntidadPersonaje entidades = juego.getNivel().getEntidades();
        assert entidades.buscar("buzo") == juego.getNivel().getBuzo();
        assert entidades.buscar("botiquin") == juego.getNivel().getBotiquin();
        assert entidades.buscar("boya") == juego.getNivel().getBoya();
        assert entidades.buscar("coral1") == juego.getNivel().getObstaculos().get(0);
        assert juego.getNivel().getObstaculos().size() == 25;
        assert juego.getNivel().getTiburones().length == 6;
        for (int i = 1; i <= 6; i++) {
            assert entidades.getTiburon(i) == juego.getNivel().getTiburones()[i - 1];
        }
        boolean error = false;
        try { entidades.buscar("noExiste"); }
        catch (IllegalArgumentException e) { error = true; }
        assert error;

        // Recogemos las tres perlas.
        for (int i = 0; i < 3; i++) {
            Recompensa perla = juego.getNivel().getPerlas()[i];
            juego.getNivel().getBuzo().setPosicion(perla.getPosicionX() - 1, perla.getPosicionY());
            juego.moverJugador("este");
            assert perla.isRecogida();
        }
        assert juego.getNivel().getCantidadPerlas() == juego.getNivel().getTotalPerlas();
        juego.getNivel().getBuzo().setPosicion(8, 8);
        juego.moverJugador("sur");
        assert entidades.getBoya().isActivado();
        juego.getNivel().getBuzo().recibirDaño(3);
        juego.revisarVida();
        assert juego.getNivel().getEntidades() != entidades;
        assert juego.getNivel().getBuzo().getVida() == 3;
        assert juego.getNivel().getBuzo().getPosicionX() == 8;
        assert juego.getNivel().getBuzo().getPosicionY() == 9;
        assert juego.getNivel().getCantidadPerlas() == 3;
        assert juego.getNivel().getEntidades().buscar("buzo") == juego.getNivel().getBuzo();
        juego.getNivel().getBuzo().setPosicion(10, 9);
        juego.moverJugador("sur");
        assert juego.isTermino();
        assert juego.getMensaje().contains("Ganaste");
        juego.iniciarJuego();
        assert juego.getNivel().getCantidadPerlas() == 0;
        assert !juego.getNivel().getEntidades().getBoya().isActivado();
        assert juego.getNivel().getBuzo().getPosicionX() == 1;
        System.out.println("Identificaciones, perlas, guardado, recuperacion y victoria correctos.");
    }
}
