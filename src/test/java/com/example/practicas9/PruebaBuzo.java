package com.example.practicas9;

public class PruebaBuzo {
    public static void main(String[] args) {
        Main juego = new Main();
        juego.iniciarJuego();
        Buzo buzo = juego.getNivel().getBuzo();
        Nivel nivel = juego.getNivel();
        assert buzo == nivel.getEntidades().getBuzo();
        assert nivel.getBuzo().getInventario().getItems().size() == 1;
        assert nivel.getBuzo().getInventario().getItems().get(0) instanceof Arma;

        assert buzo.mover(nivel, "este");
        assert buzo.mismaPosicion(2, 1);
        buzo.setPosicion(2, 2);
        assert !buzo.mover(nivel, "sur");
        assert buzo.mismaPosicion(2, 2);
        buzo.setPosicion(1, 1);
        assert !buzo.mover(nivel, "oeste");

        Tiburon tiburon = new Tiburon("Prueba", 2, 1);
        buzo.atacar(new Tiburon[]{tiburon});
        assert tiburon.getVida() == 25;
        buzo.atacar(new Tiburon[]{tiburon});
        assert tiburon.getVida() == 0;

        buzo.recibirDaño(1);
        buzo.setPosicion(3, 6);
        buzo.usarBotiquin(nivel.getBotiquin());
        assert buzo.getVida() == 3 && nivel.getBotiquin().isUsada();

        Recompensa perla = nivel.getPerlas()[0];
        buzo.setPosicion(perla.getPosicionX(), perla.getPosicionY());
        assert buzo.recogerPerlas(nivel.getPerlas()) != null;
        assert perla.isRecogida();
        assert buzo.recogerPerlas(nivel.getPerlas()) == null;
        assert nivel.getBuzo().getInventario().getItems().size() == 2;
        assert buzo.guardarEstado().equals("5,1,3");
        Buzo otro = new Buzo(3, 1, 1);
        assert otro.getInventario() != buzo.getInventario();
        assert otro.getInventario().getItems().size() == 1;
        juego.iniciarJuego();
        assert juego.getNivel().getBuzo().getInventario().getItems().size() == 1;
        assert buzo.getInventario().getItems().size() == 2;
        System.out.println("Buzo: movimiento, ataques, curacion y perlas correctos.");
    }
}
