package com.example.practicas9;

import java.util.Random;

//Enemigos del buzo
public class Tiburon extends Personaje {

    // CONFIGURACION_TIBURONES
    private final int velocidadMs = 550; //Menos ms, mas rapido
    private final int danioMordida = 1;
    private final String[] direcciones = {"norte", "sur", "este", "oeste"};
    private final Random azar = new Random();

    //Nivel crea los tiburones con 50 de vida
    public Tiburon(String nombre, int x, int y) {
        super(nombre, 50, x, y);
    }

    //Main obtiene la velocidad para el reloj de Interfaz
    public int getVelocidadMs() { return velocidadMs; }

    //Evita que dos tiburones vivos se encimen
    private boolean hayOtroTiburon(Tiburon[] tiburones, int x, int y) {
        for (Tiburon otro : tiburones) {
            if (otro != this && otro.getVida() > 0 && otro.mismaPosicion(x, y))
                return true;
        }
        return false;
    }

    // MORDER - Quita vida si alcanza al buzo
    public boolean morder(Personaje buzo) {
        if (getVida() == 0 || !mismaPosicion(buzo))
            return false;

        buzo.recibirDaño(danioMordida);
        return true;
    }

    // MOVIMIENTO_ALEATORIO - Busca una casilla libre
    public void moverAleatorio(Nivel nivel, Tiburon[] tiburones) {
        if (getVida() == 0) return;

        int inicio = azar.nextInt(direcciones.length);

        for (int intento = 0; intento < direcciones.length; intento++) {

            //Recorre las direcciones desde una al azar
            String dir = direcciones[(inicio + intento) % direcciones.length];
            int[] nuevo = siguienteLugar(dir);

            //Nivel revisa los corales y limites
            if (nivel.lugarLibre(nuevo[0], nuevo[1]) && !hayOtroTiburon(tiburones, nuevo[0], nuevo[1])) {
                mover(dir, 1);
                break;
            }
        }
    }

    //Main llama este metodo en cada turno
    public String jugarTurno(Nivel nivel, Tiburon[] tiburones, Personaje buzo, boolean mordido) {
        if (getVida() == 0) return null;

        String mensaje = null;

        //Intenta morder antes de moverse
        if (!mordido && morder(buzo)) mensaje = mensajeMordida();

        moverAleatorio(nivel, tiburones);

        //Intenta morder despues de moverse
        if (!mordido && mensaje == null && morder(buzo)) mensaje = mensajeMordida();

        return mensaje;
    }

    //Arma llama este metodo al atacar al tiburon
    public String recibirAtaque(Arma arma) {
        recibirDaño(arma.getDaño());

        if (getVida() == 0) return "¡" + getNombre() + " fue derrotado!";

        return "Impactaste a " + getNombre() + ". Vida restante: " + getVida();
    }

    //Mensaje que recibe Main al perder vida
    private String mensajeMordida() {
        return "¡" + getNombre() + " te mordió! Perdiste " + danioMordida
                + (danioMordida == 1 ? " vida." : " vidas.");
    }
}