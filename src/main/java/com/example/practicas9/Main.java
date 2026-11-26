package com.example.practicas9;

//Control general del juego
public class Main {
    private Nivel nivel; //Elementos del nivel
    private boolean termino; //Partida terminada o no
    private String mensaje; //Mensajes para el jugador

    //Interfaz inicia una partida nueva
    public void iniciarJuego() {
        nivel = new Nivel(); //El buzo ya tiene su inventario y arma
        termino = false; //No ha terminado
        mensaje = "Recoge las " + nivel.getTotalPerlas() + " perlas y llega al submarino.";
    }

    // GUARDAR_PARTIDA - Nivel guarda al tocar la boya
    private boolean guardarPartida() {
        try {
            nivel.guardar();
            return true; //Guardado correctamente
        } catch (java.io.IOException e) {
            mensaje = "Error al guardar: " + e.getMessage();
            return false; //No se guardo por error
        }
    }

    // CARGAR_PARTIDA - Nivel recupera el progreso
    public boolean cargarPartida() {
        try {
            nivel.cargar();
            termino = false; //Seguimos jugando
            mensaje = "Partida cargada desde partida.txt."; //Mensaje
            return true;
        } catch (java.io.IOException | IllegalArgumentException e) {
            return false;
        }
    }

    //Interfaz llama este metodo con WASD
    public void moverJugador(String direccion) {
        if (termino) return; //Ya no se mueve si termino la partida
        if (nivel.getBuzo().mover(nivel, direccion)) {
            mensaje = "Te moviste al " + direccion + ".";
            revisarObjetivos(); //Revisa perlas, boya y salida
            revisarVida(); //Revisa vidas
        } else {
            mensaje = "No puedes pasar por ahí.";
        }
    }

    // MOVIMIENTO_TIBURONES - Interfaz llama este metodo con el reloj
    public void moverTiburones() {
        if (termino) return;
        //Cada tiburon hace su turno, solo permitimos una mordida
        boolean mordido = false;
        Tiburon[] tiburones = nivel.getTiburones();
        for (Tiburon tiburon : tiburones) {
            String mordida = tiburon.jugarTurno(nivel, tiburones, nivel.getBuzo(), mordido);
            if (mordida != null) {
                mordido = true;
                mensaje = mordida;
            }
        }
        revisarVida(); //Revisamos las vidas al terminar el turno
    }

    //Interfaz llama este metodo con ESPACIO
    public void atacar() {
        if (termino) return; // no puede atacar
        mensaje = nivel.getBuzo().atacar(nivel.getTiburones());
        revisarVida();
    }

    //Interfaz llama este metodo con F
    public void usarBotiquin() {
        if (termino) return; // se termino no se usa
        mensaje = nivel.getBuzo().usarBotiquin(nivel.getBotiquin());
    }

    // OBJETIVOS - Revisa perlas, boya y salida
    private void revisarObjetivos() {
        int x = nivel.getBuzo().getPosicionX(), y = nivel.getBuzo().getPosicionY();
        String recogida = nivel.getBuzo().recogerPerlas(nivel.getPerlas());
        if (recogida != null) mensaje = recogida;

        // CHECKPOINT - Guarda al llegar a la boya
        CheckPoint boyaGuardado = nivel.getBoya();
        if (boyaGuardado.mismaPosicion(x, y)) {
            boolean estabaActivada = boyaGuardado.isActivado();
            boyaGuardado.activar();
            if (guardarPartida()) {
                mensaje = "¡Partida guardada! El archivo partida.txt se actualizó.";
            } else if (!estabaActivada) {
                boyaGuardado.desactivar(); //No activamos la boya si no se guardo
            }
        }

        // VICTORIA - Revisa si llego con todas las perlas
        if (x == nivel.getSalidaX() && y == nivel.getSalidaY()) {
            if (nivel.getCantidadPerlas() < nivel.getTotalPerlas()) {
                mensaje = "Faltan perlas para subir al submarino.";
            } else {
                termino = true;
                mensaje = "¡Ganaste! Llegaste al submarino con todas las perlas.";
            }
        }
    }

    // DERROTA - Revisa las vidas del buzo
    private void revisarVida() {
        if (nivel.getBuzo().getVida() > 0) return; //Todavia sigue vivo
        if (nivel.getBoya().isActivado()) { //Revisa si guardo en la boya
            iniciarJuego(); //Crea un nivel para recuperar el guardado
            if (cargarPartida()) { //Recupera las vidas y el progreso
                mensaje = "¡La boya te salvó! Regresaste al último guardado.";
                return;
            }
        }
        termino = true;
        mensaje = "Te quedaste sin vidas. Presiona R para volver al menú.";
    }

    //Getters del juego para la interfaz
    public Nivel getNivel() { return nivel; }
    public int getVelocidadTiburones() {
        Tiburon[] tiburones = nivel.getTiburones();
        return tiburones.length == 0 ? 550 : tiburones[0].getVelocidadMs();
    }
    public String getMensaje() { return mensaje; }
    public boolean isTermino() { return termino; }

    //Inicia la ventana de JavaFX
    public static void main(String[] args) {
        Interfaz.main(args);
    }
}
