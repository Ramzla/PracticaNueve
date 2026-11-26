package com.example.practicas9;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

// Ejecutar con -ea en una carpeta de pruebas, sobrescribe partida.txt.
public class PruebaNivel {
    public static void main(String[] args) throws Exception {
        Main juego = new Main();
        juego.iniciarJuego();
        Nivel nivel = juego.getNivel();
        assert nivel.getSalidaX() == 10 && nivel.getSalidaY() == 10;
        juego.getNivel().getBuzo().recibirDaño(1);
        juego.getNivel().getTiburones()[0].recibirDaño(25);
        juego.getNivel().getBotiquin().usar();
        nivel.getObstaculos().get(0).destruye();
        nivel.guardar();
        juego.iniciarJuego();
        assert juego.cargarPartida();
        assert juego.getNivel().getBuzo().getVida() == 2;
        assert juego.getNivel().getTiburones()[0].getVida() == 25;
        assert juego.getNivel().getBotiquin().isUsada();
        assert juego.getNivel().getObstaculos().get(0).isDestruido();

        Properties datos = new Properties();
        try (FileReader reader = new FileReader("partida.txt")) { datos.load(reader); }
        datos.setProperty("buzo", "8,9,999");
        try (FileWriter writer = new FileWriter("partida.txt")) { datos.store(writer, "Prueba inválida"); }
        juego.iniciarJuego();
        assert !juego.cargarPartida();
        assert juego.getNivel().getBuzo().getVida() == juego.getNivel().getBuzo().getVidaMaxima();
        assert juego.getNivel().getBuzo().getPosicionX() == 1;
        System.out.println("Nivel: guardado, carga y validación correctos.");
    }
}
