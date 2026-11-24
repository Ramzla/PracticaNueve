package com.example.practicas9;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static final int COLUMNAS = 12;
    public static final int FILAS = 12;
    public static final int MAX_VIDAS = 3;

    private final int[][] lugaresPerlas = {{5, 1}, {9, 2}, {2, 9}};
    private final int[][] lugaresTiburones = {
            {8, 2}, {9, 4}, {3, 6}, {9, 8}, {5, 9}, {2, 7}
    };
    private final int[][] corales = {
            {2, 3}, {3, 3}, {4, 3}, {6, 2}, {6, 3}, {6, 4},
            {8, 4}, {4, 6}, {5, 6}, {7, 7}, {2, 5}, {3, 5},
            {7, 5}, {9, 5}, {2, 8}, {4, 8}, {6, 8}, {7, 8},
            {4, 9}, {6, 9}, {2, 10}, {6, 10}, {8, 10}, {9, 7}, {8, 3}
    };
    private final Random azar = new Random();
    private final int columnas = COLUMNAS, filas = FILAS, maxVidas = MAX_VIDAS;

    private Nivel nivel;
    private Personaje jugador;
    private Personaje[] tiburones;
    private Jefe jefe;
    private long ultimaMordidaJefe;
    private Utileria botiquin;
    private Arma arma;
    private Recompensa[] perlas;
    private boolean[] recogidas;
    private int cantidadPerlas;
    private boolean termino;
    private String mensaje;

    public void iniciarJuego() {
        Inventario inventario = new Inventario(5);
        arma = new Arma("Arpón", 25, 1);
        inventario.agregarItem(arma);

        nivel = new Nivel("Arrecife profundo", 1, "Normal", inventario);
        jugador = new Personaje("Buzo", maxVidas, 1, 1);
        nivel.agregarElementoDinamico(jugador);

        tiburones = new Personaje[lugaresTiburones.length];
        for (int i = 0; i < tiburones.length; i++) {
            tiburones[i] = new Personaje("Tiburón " + (i + 1), 50,
                    lugaresTiburones[i][0], lugaresTiburones[i][1]);
            nivel.agregarElementoDinamico(tiburones[i]);
        }
        jefe = new Jefe(9, 9);
        ultimaMordidaJefe = 0;
        botiquin = new Utileria("Botiquín marino", "Recupera 1 vida", 3, 7);
        nivel.agregarElementoDinamico(botiquin);
        nivel.agregarCheckPoint(new CheckPoint("Boya de guardado", 8, 9));

        for (int[] p : corales) {
            nivel.agregarObstaculo(new Obstaculo("Coral", 10, p[0], p[1]));
        }

        perlas = new Recompensa[] {
                new Recompensa("Perla azul", 100, "tesoro"),
                new Recompensa("Perla roja", 100, "tesoro"),
                new Recompensa("Perla verde", 100, "tesoro")
        };
        recogidas = new boolean[3];
        cantidadPerlas = 0;
        termino = false;
        mensaje = "Recoge 3 perlas, derrota al jefe y llega al submarino.";
    }

    private boolean guardarPartida() {
        try {
            ArchivoPartida.guardar(nivel, jugador, tiburones, botiquin, recogidas, lugaresPerlas, jefe);
            return true;
        } catch (java.io.IOException e) {
            mensaje = "Error al guardar: " + e.getMessage();
            return false;
        }
    }

    public boolean cargarPartida() {
        try {
            cantidadPerlas = ArchivoPartida.cargar(
                    nivel, jugador, tiburones, botiquin, perlas, recogidas,
                    lugaresPerlas, maxVidas, jefe);
            termino = false;
            ultimaMordidaJefe = 0;
            mensaje = "Partida cargada desde partida.txt.";
            return true;
        } catch (java.io.IOException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean lugarLibre(int x, int y) {
        if (x < 1 || y < 1 || x >= columnas - 1 || y >= filas - 1) return false;
        for (Obstaculo obstaculo : nivel.getObstaculos()) {
            if (!obstaculo.isDestruido() && mismaCasilla(obstaculo, x, y)) return false;
        }
        return true;
    }

    private int[] siguienteLugar(String direccion, int x, int y) {
        return switch (direccion) {
            case "norte" -> new int[]{x, y - 1};
            case "sur" -> new int[]{x, y + 1};
            case "este" -> new int[]{x + 1, y};
            default -> new int[]{x - 1, y};
        };
    }

    private boolean mismaCasilla(EntidadMapa objeto, int x, int y) {
        return objeto.getPosicionX() == x && objeto.getPosicionY() == y;
    }

    private int distancia(EntidadMapa a, EntidadMapa b) {
        return Math.abs(a.getPosicionX() - b.getPosicionX())
                + Math.abs(a.getPosicionY() - b.getPosicionY());
    }

    public void moverJugador(String direccion) {
        if (termino) return;
        int[] nuevo = siguienteLugar(direccion, jugador.getPosicionX(), jugador.getPosicionY());
        if (!lugarLibre(nuevo[0], nuevo[1])) {
            mensaje = "No puedes pasar por ahí.";
        } else {
            jugador.mover(direccion, 1);
            mensaje = "Te moviste al " + direccion + ".";
            revisarObjetivos();
            revisarVida();
        }
    }

    private boolean hayOtroTiburon(Personaje actual, int x, int y) {
        for (Personaje otro : tiburones) {
            if (otro != actual && otro.getVida() > 0 && mismaCasilla(otro, x, y)) return true;
        }
        return false;
    }

    private boolean morder(Personaje tiburon) {
        if (!mismaCasilla(tiburon, jugador.getPosicionX(), jugador.getPosicionY())) return false;
        jugador.recibirDaño(1);
        mensaje = "¡" + tiburon.getNombre() + " te mordió! Perdiste 1 vida.";
        return true;
    }

    public void moverTiburones() {
        if (termino || isJefeActivo()) return;
        String[] direcciones = {"norte", "sur", "este", "oeste"};
        boolean mordido = false;

        for (Personaje tiburon : tiburones) {
            if (tiburon.getVida() == 0) continue;
            if (!mordido) mordido = morder(tiburon);

            int inicio = azar.nextInt(4);
            for (int intento = 0; intento < 4; intento++) {
                String direccion = direcciones[(inicio + intento) % 4];
                int[] nuevo = siguienteLugar(direccion,
                        tiburon.getPosicionX(), tiburon.getPosicionY());

                if (lugarLibre(nuevo[0], nuevo[1])
                        && !hayOtroTiburon(tiburon, nuevo[0], nuevo[1])) {
                    tiburon.mover(direccion, 1);
                    break;
                }
            }

            if (!mordido) mordido = morder(tiburon);
        }
    }

    public boolean isJefeActivo() {
        return cantidadPerlas == perlas.length;
    }

    private String caminoHaciaBuzo() {
        int[][] distancias = new int[columnas][filas];
        for (int[] fila : distancias) Arrays.fill(fila, -1);
        ArrayDeque<int[]> cola = new ArrayDeque<>();
        int x = jugador.getPosicionX(), y = jugador.getPosicionY();
        if (!lugarLibre(x, y)) return null;
        distancias[x][y] = 0;
        cola.add(new int[]{x, y});
        String[] direcciones = {"norte", "sur", "este", "oeste"};
        while (!cola.isEmpty()) {
            int[] punto = cola.remove();
            for (String dir : direcciones) {
                int[] nuevo = siguienteLugar(dir, punto[0], punto[1]);
                if (lugarLibre(nuevo[0], nuevo[1]) && distancias[nuevo[0]][nuevo[1]] == -1) {
                    distancias[nuevo[0]][nuevo[1]] = distancias[punto[0]][punto[1]] + 1;
                    cola.add(nuevo);
                }
            }
        }

        String mejor = null;
        int minimo = Integer.MAX_VALUE;
        for (String dir : direcciones) {
            int[] nuevo = siguienteLugar(dir, jefe.getPosicionX(), jefe.getPosicionY());
            if (lugarLibre(nuevo[0], nuevo[1]) && distancias[nuevo[0]][nuevo[1]] >= 0
                    && distancias[nuevo[0]][nuevo[1]] < minimo) {
                minimo = distancias[nuevo[0]][nuevo[1]];
                mejor = dir;
            }
        }
        return mejor;
    }

    public void moverJefe() {
        if (termino || !isJefeActivo() || jefe.getVida() == 0) return;
        if (!mismaCasilla(jefe, jugador.getPosicionX(), jugador.getPosicionY())) {
            String direccion = caminoHaciaBuzo();
            if (direccion != null) jefe.mover(direccion, 1);
        }

        long ahora = System.currentTimeMillis();
        if (mismaCasilla(jefe, jugador.getPosicionX(), jugador.getPosicionY())
                && ahora - ultimaMordidaJefe >= 1100) {
            jugador.recibirDaño(1);
            ultimaMordidaJefe = ahora;
            mensaje = "¡El tiburón gigante te mordió! Perdiste 1 vida.";
        }
    }

    public void atacar() {
        if (termino) return;
        if (isJefeActivo()) {
            if (jefe.getVida() == 0) {
                mensaje = "El jefe fue derrotado. Ve al submarino.";
            } else if (distancia(jugador, jefe) <= arma.getAlcance()) {
                jefe.recibirDaño(arma.getDaño());
                mensaje = jefe.getVida() == 0
                        ? "¡Derrotaste al tiburón gigante! Ve al submarino."
                        : "¡Impactaste al jefe! Vida restante: " + jefe.getVida();
            } else {
                mensaje = "Acércate al tiburón gigante para atacarlo.";
            }
            return;
        }
        Personaje objetivo = null;
        int menorDistancia = Integer.MAX_VALUE;
        for (Personaje tiburon : tiburones) {
            int d = distancia(jugador, tiburon);
            if (tiburon.getVida() > 0 && d <= arma.getAlcance() && d < menorDistancia) {
                objetivo = tiburon;
                menorDistancia = d;
            }
        }
        if (objetivo == null) {
            mensaje = "Para atacar, acércate a cualquiera de los tiburones.";
        } else {
            objetivo.recibirDaño(arma.getDaño());
            mensaje = objetivo.getVida() == 0
                    ? "¡" + objetivo.getNombre() + " fue derrotado!"
                    : "Impactaste a " + objetivo.getNombre() + ". Vida restante: " + objetivo.getVida();
        }
        revisarVida();
    }

    public void usarBotiquin() {
        if (termino) return;
        if (distancia(jugador, botiquin) > 1 || botiquin.isUsada()) {
            mensaje = "Acércate al botiquín verde. Solo puedes usarlo una vez.";
        } else if (jugador.getVida() == maxVidas) {
            mensaje = "Ya tienes las 3 vidas completas.";
        } else {
            botiquin.usar();
            jugador.reparar(1);
            mensaje = "Recuperaste una vida. Vidas: " + jugador.getVida() + "/" + maxVidas;
        }
    }

    private void revisarObjetivos() {
        int x = jugador.getPosicionX(), y = jugador.getPosicionY();
        for (int i = 0; i < perlas.length; i++) {
            if (!recogidas[i] && lugaresPerlas[i][0] == x && lugaresPerlas[i][1] == y
                    && nivel.getInventario().agregarItem(perlas[i])) {
                recogidas[i] = true;
                cantidadPerlas++;
                mensaje = "Recogiste " + perlas[i].getNombre() + ".";
            }
        }

        if (isJefeActivo() && jefe.getVida() > 0 && cantidadPerlas == perlas.length) {
            if (mensaje.startsWith("Recogiste ")) {
                mensaje = "¡Apareció el jefe! Los otros tiburones huyeron. Derrota al gigante.";
            }
        }

        CheckPoint boyaGuardado = nivel.getCheckPoints().get(0);
        if (mismaCasilla(boyaGuardado, x, y)) {
            boyaGuardado.activar();
            if (guardarPartida()) {
                mensaje = "¡Partida guardada! El archivo partida.txt se actualizó.";
            }
        }

        if (x == 10 && y == 10) {
            if (cantidadPerlas < perlas.length) {
                mensaje = "Faltan perlas para subir al submarino.";
            } else if (jefe.getVida() > 0) {
                mensaje = "Debes derrotar al tiburón gigante para subir al submarino.";
            } else {
                termino = true;
                mensaje = "¡Ganaste! Derrotaste al jefe y rescataste las tres perlas.";
            }
        }
    }

    public void revisarVida() {
        if (jugador.getVida() > 0) return;
        if (nivel.getCheckPoints().get(0).isActivado()) {
            iniciarJuego();
            if (cargarPartida()) {
                mensaje = "¡La boya te salvó! Regresaste al último guardado.";
                return;
            }
        }
        termino = true;
        mensaje = "Te quedaste sin vidas. Presiona R para volver al menú.";
    }


    public Jefe getJefe() { return jefe; }
    public Nivel getNivel() { return nivel; }
    public Personaje getJugador() { return jugador; }
    public Personaje[] getTiburones() { return tiburones; }
    public Utileria getBotiquin() { return botiquin; }
    public Recompensa[] getPerlas() { return perlas; }
    public boolean[] getRecogidas() { return recogidas; }
    public int[][] getLugaresPerlas() { return lugaresPerlas; }
    public int getCantidadPerlas() { return cantidadPerlas; }
    public String getMensaje() { return mensaje; }
    public boolean isTermino() { return termino; }

    public static void main(String[] args) {
        Interfaz.main(args);
    }
}
