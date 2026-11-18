package com.example.practicas9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ArchivoPartida {
    private static final String ARCHIVO = "partida.txt";

    public static void guardar(Nivel nivel, Personaje buzo, Personaje[] tiburones,
                               Utileria botiquin, boolean[] recogidas, int[][] lugaresPerlas)
            throws IOException {
        Properties datos = new Properties();
        datos.setProperty("buzo", buzo.getPosicionX() + "," + buzo.getPosicionY() + "," + buzo.getVida());

        CheckPoint boya = nivel.getCheckPoints().get(0);
        datos.setProperty("boya", boya.getPosicionX() + "," + boya.getPosicionY() + "," + boya.isActivado());
        datos.setProperty("botiquin", botiquin.getPosicionX() + "," + botiquin.getPosicionY() + "," + botiquin.isUsada());

        datos.setProperty("cantidadTiburones", String.valueOf(tiburones.length));
        for (int i = 0; i < tiburones.length; i++) {
            Personaje t = tiburones[i];
            datos.setProperty("tiburon." + i, t.getPosicionX() + "," + t.getPosicionY() + "," + t.getVida());
        }

        datos.setProperty("cantidadPerlas", String.valueOf(recogidas.length));
        for (int i = 0; i < recogidas.length; i++) {
            datos.setProperty("perla." + i,
                    lugaresPerlas[i][0] + "," + lugaresPerlas[i][1] + "," + recogidas[i]);
        }

        List<Obstaculo> corales = nivel.getObstaculos();
        datos.setProperty("cantidadCorales", String.valueOf(corales.size()));
        for (int i = 0; i < corales.size(); i++) {
            Obstaculo c = corales.get(i);
            datos.setProperty("coral." + i,
                    c.getPosicionX() + "," + c.getPosicionY() + "," + c.isDestruido());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO))) {
            datos.store(writer, "Rescate Marino - estado del juego");
        }
    }

    public static int cargar(Nivel nivel, Personaje buzo, Personaje[] tiburones,
                             Utileria botiquin, Recompensa[] perlas, boolean[] recogidas,
                             int[][] lugaresPerlas, int maxVidas) throws IOException {
        Properties datos = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            datos.load(reader);
        }

        int[] b = numeros(datos, "buzo");
        if (b[2] < 1 || b[2] > maxVidas) throw new IOException("Vidas inválidas");
        int[] boya = coordenadas(datos, "boya");
        int[] bot = coordenadas(datos, "botiquin");
        boolean boyaActivada = booleano(datos, "boya");
        boolean botiquinUsado = booleano(datos, "botiquin");

        if (Integer.parseInt(datos.getProperty("cantidadTiburones", "-1")) != tiburones.length
                || Integer.parseInt(datos.getProperty("cantidadPerlas", "-1")) != perlas.length
                || Integer.parseInt(datos.getProperty("cantidadCorales", "-1")) != nivel.getObstaculos().size()) {
            throw new IOException("La cantidad de elementos no coincide");
        }

        int[][] enemigos = new int[tiburones.length][];
        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = numeros(datos, "tiburon." + i);
            if (enemigos[i][2] < 0 || enemigos[i][2] > 50) throw new IOException("Vida de tiburón inválida");
        }

        int[][] posicionPerlas = new int[perlas.length][];
        boolean[] perlasRecogidas = new boolean[perlas.length];
        for (int i = 0; i < perlas.length; i++) {
            posicionPerlas[i] = coordenadas(datos, "perla." + i);
            perlasRecogidas[i] = booleano(datos, "perla." + i);
        }

        List<Obstaculo> corales = nivel.getObstaculos();
        int[][] posicionesCorales = new int[corales.size()][];
        boolean[] coralesDestruidos = new boolean[corales.size()];
        for (int i = 0; i < corales.size(); i++) {
            posicionesCorales[i] = coordenadas(datos, "coral." + i);
            coralesDestruidos[i] = booleano(datos, "coral." + i);
        }

        buzo.setPosicion(b[0], b[1]);
        buzo.recibirDaño(maxVidas - b[2]);
        nivel.getCheckPoints().get(0).setPosicion(boya[0], boya[1]);
        if (boyaActivada) nivel.getCheckPoints().get(0).activar();
        botiquin.setPosicion(bot[0], bot[1]);
        if (botiquinUsado) botiquin.usar();

        for (int i = 0; i < tiburones.length; i++) {
            tiburones[i].setPosicion(enemigos[i][0], enemigos[i][1]);
            tiburones[i].recibirDaño(50 - enemigos[i][2]);
        }

        int total = 0;
        for (int i = 0; i < perlas.length; i++) {
            lugaresPerlas[i][0] = posicionPerlas[i][0];
            lugaresPerlas[i][1] = posicionPerlas[i][1];
            if (perlasRecogidas[i] && nivel.getInventario().agregarItem(perlas[i])) {
                recogidas[i] = true;
                total++;
            }
        }

        for (int i = 0; i < corales.size(); i++) {
            Obstaculo c = corales.get(i);
            c.setPosicion(posicionesCorales[i][0], posicionesCorales[i][1]);
            if (coralesDestruidos[i]) c.destruye();
        }
        return total;
    }

    private static String[] campos(Properties datos, String clave) throws IOException {
        String valor = datos.getProperty(clave);
        if (valor == null) throw new IOException("Falta " + clave);
        String[] partes = valor.split(",");
        if (partes.length != 3) throw new IOException("Formato inválido en " + clave);
        return partes;
    }

    private static int[] numeros(Properties datos, String clave) throws IOException {
        String[] p = campos(datos, clave);
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])};
    }

    private static int[] coordenadas(Properties datos, String clave) throws IOException {
        String[] p = campos(datos, clave);
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])};
    }

    private static boolean booleano(Properties datos, String clave) throws IOException {
        String valor = campos(datos, clave)[2];
        if (!valor.equals("true") && !valor.equals("false")) {
            throw new IOException("Valor lógico inválido en " + clave);
        }
        return Boolean.parseBoolean(valor);
    }
}
