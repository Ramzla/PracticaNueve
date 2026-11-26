package com.example.practicas9;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

// Ejecutar con -ea en una carpeta de pruebas, usa partida.txt.
public class PruebaRefactor {
    public static void main(String[] args) throws Exception {
        Main juego = new Main();
        juego.iniciarJuego();
        Nivel nivel = juego.getNivel();
        Personaje buzo = juego.getJugador();
        Recompensa perla = juego.getPerlas()[0];
        assert nivel.getEntidades().buscar("perla1") == perla;
        assert buzo.siguienteLugar("este")[0] == 2;
        assert !nivel.lugarLibre(2, 3) && !nivel.lugarLibre(0, 1);
        assert buzo.distancia(perla) == 4;
        buzo.reparar(100);
        assert buzo.getVida() == 3;
        Tiburon t = juego.getTiburones()[0];
        t.reparar(100);
        assert t.getVida() == 50;
        buzo.recibirDaño(1);
        juego.usarBotiquin();
        assert buzo.getVida() == 2 && !juego.getBotiquin().isUsada();
        buzo.setPosicion(3, 6);
        juego.usarBotiquin();
        assert buzo.getVida() == 3 && juego.getBotiquin().isUsada();
        buzo.setPosicion(4, 1);
        juego.moverJugador("este");
        assert perla.isRecogida() && juego.getCantidadPerlas() == 1;
        assert nivel.getInventario().getItems().contains(perla);
        nivel.guardar();
        Properties datos = new Properties();
        try (FileReader reader = new FileReader("partida.txt")) { datos.load(reader); }
        assert datos.getProperty("perla.0").equals("5,1,true");
        assert datos.getProperty("buzo").equals("5,1,3");
        assert datos.getProperty("jefe") == null;
        // Cargar sobre el mismo nivel no duplica las perlas ni el inventario.
        assert nivel.cargar() == 1;
        assert nivel.getInventario().getItems().size() == 2;
        // El guardado antiguo con un campo adicional tambien funciona.
        datos.setProperty("jefe", "9,9,100");
        try (FileWriter writer = new FileWriter("partida.txt")) { datos.store(writer, "Antiguo"); }
        juego.iniciarJuego();
        assert juego.cargarPartida() && juego.getCantidadPerlas() == 1;
        // Si una coordenada es invalida, no se modifica ningun objeto.
        datos.setProperty("coral.24", "99,3,false");
        try (FileWriter writer = new FileWriter("partida.txt")) { datos.store(writer, "Invalido"); }
        juego.iniciarJuego();
        assert !juego.cargarPartida();
        assert juego.getJugador().mismaPosicion(1, 1) && juego.getCantidadPerlas() == 0;
        assert !juego.getBotiquin().isUsada();
        System.out.println("Refactor: posiciones, vida maxima, botiquin, perlas y validacion correctos.");
    }
}
