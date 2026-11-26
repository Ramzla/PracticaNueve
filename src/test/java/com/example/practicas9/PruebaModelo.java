package com.example.practicas9;

public class PruebaModelo {
    public static void main(String[] args) {
        Inventario inventario = new Inventario(3);
        Arma arpon = new Arma("Arpón", 25, 1);
        Recompensa perla = new Recompensa("Perla", 1, 1, "perlaazul");
        assert inventario.agregarItem(arpon);
        assert inventario.agregarItem(perla);
        assert inventario.getItems().size() == 2;
        assert inventario.eliminarItem(perla);
        assert inventario.getItems().size() == 1;

        Personaje persona = new Buzo(100, 4, 4);
        persona.mover("norte", 2);
        assert persona.getPosicionY() == 2;
        persona.recibirDaño(30);
        assert persona.getVida() == 70;
        persona.reparar(20);
        assert persona.getVida() == 90;

        Obstaculo roca = new Obstaculo("Roca", 5, 5);
        roca.destruye();
        assert roca.isDestruido();
        CheckPoint baliza = new CheckPoint("Boya", 1, 1);
        baliza.activar();
        assert baliza.isActivado();
        Utileria botiquin = new Utileria("Botiquín", 3, 3);
        botiquin.usar();
        assert botiquin.isUsada();

        // Comprobamos el movimiento por medio de la interfaz, sin mover todo el nivel.
        ElementoDinamico[] dinamicos = {persona, botiquin};
        for (ElementoDinamico elemento : dinamicos) elemento.mover("este", 2);
        assert persona.getPosicionX() == 6;
        assert botiquin.getPosicionX() == 5;

        Nivel nivel = new Nivel(inventario);
        assert nivel.getBuzo().getVidaMaxima() == 3;
        assert nivel.getObstaculos().size() == 25;
        assert !nivel.lugarLibre(2, 3);
        assert nivel.getBoya().mismaPosicion(8, 9);
        assert nivel.getEntidades().buscar("buzo") == nivel.getBuzo();

        System.out.println("Pruebas de modelo terminadas correctamente.");
    }
}
