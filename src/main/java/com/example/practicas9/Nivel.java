package com.example.practicas9;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Nivel {

    //Lista de corales
    private final List<Obstaculo> obstaculos = new ArrayList<>();

    // TAMANO_MAPA - Dimensiones que usa Interfaz
    private final int columnas = 12, filas = 12;

    // SALIDA_MAPA - Posicion del submarino
    private final int salidaX = 10, salidaY = 10;

    //Registro de los objetos del mapa
    private final EntidadPersonaje entidades = new EntidadPersonaje();
    private Tiburon[] tiburones;
    private Recompensa[] perlas;

    // VIDAS_BUZO - Vidas iniciales del jugador
    private final int maxVidas = 3;
    private final String archivo = "partida.txt";

    //Main crea el nivel al iniciar
    public Nivel() {
        crearEntidades();
    }

    // (POSICIONES_MAPA - Crea los objetos del juego
    private void crearEntidades() {

        Buzo buzo = new Buzo(maxVidas, 1, 1);
        entidades.registrar("buzo", buzo);

        Utileria botiquin = new Utileria("Botiquín marino", 3, 7);
        entidades.registrar("botiquin", botiquin);

        CheckPoint boya = new CheckPoint("Boya de guardado", 8, 9);
        entidades.registrar("boya", boya);

        // POSICIONES_TIBURONES - Cantidad y posicion de los tiburones
        int[][] lugaresTiburones = {
                {8, 2}, {9, 4}, {3, 6},
                {9, 8}, {5, 9}, {2, 7}
        };

        tiburones = new Tiburon[lugaresTiburones.length];

        for (int i = 0; i < tiburones.length; i++) {
            tiburones[i] = new Tiburon("Tiburón " + (i + 1),
                    lugaresTiburones[i][0], lugaresTiburones[i][1]);

            entidades.registrar("tiburon" + (i + 1), tiburones[i]);
        }

        // CORALES_MAPA - Posiciones de los corales
        int[][] lugaresCorales = {
                {2, 3}, {3, 3}, {4, 3}, {6, 2}, {6, 3}, {6, 4},
                {8, 4}, {4, 6}, {5, 6}, {7, 7}, {2, 5}, {3, 5},
                {7, 5}, {9, 5}, {2, 8}, {4, 8}, {6, 8}, {7, 8},
                {4, 9}, {6, 9}, {2, 10}, {6, 10}, {8, 10}, {9, 7}, {8, 3}
        };

        int numeroCoral = 1;

        for (int[] posicion : lugaresCorales) {
            Obstaculo coral = new Obstaculo("Coral", posicion[0], posicion[1]);

            entidades.registrar("coral" + numeroCoral++, coral);
            agregarObstaculo(coral);
        }

        // POSICIONES_PERLAS - Nombre, posicion e imagen
        perlas = new Recompensa[]{
                new Recompensa("Perla azul", 5, 1, "perlaazul"),
                new Recompensa("Perla roja", 9, 2, "perlaroja"),
                new Recompensa("Perla verde", 2, 9, "perlaverde")
        };

        for (int i = 0; i < perlas.length; i++)
            entidades.registrar("perla" + (i + 1), perlas[i]);
    }

    //Interfaz obtiene el tamaño del mapa
    public int getColumnas() { return columnas; }
    public int getFilas() { return filas; }

    // LIMITES_MAPA - Evita que los personajes salgan del mapa
    public boolean dentroMapa(int x, int y) {
        return x >= 1 && y >= 1 && x < columnas - 1 && y < filas - 1;
    }

    // LUGAR_LIBRE - Personaje y Tiburon revisan si pueden avanzar
    public boolean lugarLibre(int x, int y) {
        if (!dentroMapa(x, y)) return false;

        for (Obstaculo coral : obstaculos) {
            if (!coral.isDestruido() && coral.mismaPosicion(x, y))
                return false;
        }

        return true;
    }

    //Main e Interfaz consultan las perlas recogidas
    public int getCantidadPerlas() {
        int total = 0;

        for (Recompensa perla : perlas)
            if (perla.isRecogida()) total++;

        return total;
    }

    //Obtiene los objetos registrados en EntidadPersonaje
    public EntidadPersonaje getEntidades() { return entidades; }
    public Buzo getBuzo() { return entidades.getBuzo(); }
    public Utileria getBotiquin() { return entidades.getBotiquin(); }
    public CheckPoint getBoya() { return entidades.getBoya(); }

    //Main e Interfaz consultan los objetos del mapa
    public Tiburon[] getTiburones() { return tiburones.clone(); }
    public Recompensa[] getPerlas() { return perlas.clone(); }
    public int getTotalPerlas() { return perlas.length; }
    public int getSalidaX() { return salidaX; }
    public int getSalidaY() { return salidaY; }

    //Interfaz consulta los corales para dibujarlos
    public List<Obstaculo> getObstaculos() {
        return Collections.unmodifiableList(obstaculos);
    }

    //Agrega un coral a la lista
    public void agregarObstaculo(Obstaculo obstaculo) {
        obstaculos.add(Objects.requireNonNull(obstaculo));
    }

    // OBJETOS_GUARDADO - Identifica los objetos del archivo
    private Map<String, EntidadMapa> objetosGuardado() {

        Map<String, EntidadMapa> objetos = new LinkedHashMap<>();

        objetos.put("buzo", getBuzo());
        objetos.put("boya", getBoya());
        objetos.put("botiquin", getBotiquin());

        for (int i = 0; i < tiburones.length; i++)
            objetos.put("tiburon." + i, tiburones[i]);

        for (int i = 0; i < obstaculos.size(); i++)
            objetos.put("coral." + i, obstaculos.get(i));

        for (int i = 0; i < perlas.length; i++)
            objetos.put("perla." + i, perlas[i]);

        return objetos;
    }

    // GUARDAR_PARTIDA - Main lo llama al tocar la boya
    public void guardar() throws IOException {

        Properties datos = new Properties();

        //Obtiene el estado de cada objeto
        objetosGuardado().forEach((clave, objeto) ->
                datos.setProperty(clave, objeto.guardarEstado()));

        datos.setProperty("cantidadTiburones", String.valueOf(tiburones.length));
        datos.setProperty("cantidadCorales", String.valueOf(obstaculos.size()));
        datos.setProperty("cantidadPerlas", String.valueOf(perlas.length));

        //Crea un archivo temporal para proteger el guardado anterior
        Path destino = Path.of(archivo).toAbsolutePath();
        Path temporal = Files.createTempFile(destino.getParent(), "partida-", ".tmp");

        try {
            //Escribe los datos en el archivo temporal
            try (FileWriter writer = new FileWriter(temporal.toFile())) {
                datos.store(writer, "Rescate Marino - estado del juego");
            }

            //Reemplaza el guardado anterior
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }

        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    // CARGAR_PARTIDA - Main recupera el progreso guardado
    public int cargar() throws IOException {

        Properties datos = new Properties();

        //Lee el archivo guardado
        try (FileReader reader = new FileReader(archivo)) {
            datos.load(reader);
        }

        //Revisa la cantidad de objetos guardados
        validarCantidad(datos, "Tiburones", tiburones.length);
        validarCantidad(datos, "Corales", obstaculos.size());
        validarCantidad(datos, "Perlas", perlas.length);

        Map<String, EntidadMapa> objetos = objetosGuardado();
        Map<EntidadMapa, String[]> estados = new LinkedHashMap<>();

        //Revisa los datos de cada objeto
        for (String clave : objetos.keySet()) {

            String valor = datos.getProperty(clave);

            if (valor == null)
                throw new IOException("Falta " + clave);

            String[] campos = valor.split(",", -1);
            EntidadMapa objeto = objetos.get(clave);

            objeto.validarEstado(campos);

            //Revisa que la posicion este dentro del mapa
            if (!dentroMapa(Integer.parseInt(campos[0]), Integer.parseInt(campos[1])))
                throw new IOException("Posición inválida: " + clave);

            //Evita cargar una partida con el buzo muerto
            if (clave.equals("buzo") && Integer.parseInt(campos[2]) == 0)
                throw new IOException("Vidas inválidas del buzo");

            estados.put(objeto, campos);
        }

        validarColisiones(estados);

        //Revisa el espacio del inventario del buzo
        Inventario inventario = getBuzo().getInventario();
        int ocupados = inventario.getItems().size();

        for (Recompensa perla : perlas) {
            if (inventario.getItems().contains(perla)) ocupados--;
            if (Boolean.parseBoolean(estados.get(perla)[2])) ocupados++;
        }

        if (ocupados > inventario.getCapacidadMaxima())
            throw new IOException("Inventario lleno");

        //Retira las perlas actuales del inventario
        for (Recompensa perla : perlas)
            inventario.eliminarItem(perla);

        //Recupera el estado de cada objeto
        estados.forEach((objeto, campos) -> objeto.restaurarEstado(campos));

        //Agrega las perlas que estaban guardadas
        for (Recompensa perla : perlas) {
            if (perla.isRecogida()) inventario.agregarItem(perla);
        }

        return getCantidadPerlas();
    }

    // VALIDAR_COLISIONES - Revisa las posiciones antes de cargar
    private void validarColisiones(Map<EntidadMapa, String[]> estados) throws IOException {

        boolean[][] corales = new boolean[columnas][filas];
        boolean[][] ocupadas = new boolean[columnas][filas];

        //Marca las casillas de los corales sin destruir
        for (Obstaculo coral : obstaculos) {
            String[] campos = estados.get(coral);

            if (!Boolean.parseBoolean(campos[2]))
                corales[Integer.parseInt(campos[0])][Integer.parseInt(campos[1])] = true;
        }

        //Evita cargar al buzo dentro de un coral
        String[] buzo = estados.get(getBuzo());

        if (corales[Integer.parseInt(buzo[0])][Integer.parseInt(buzo[1])])
            throw new IOException("El buzo está dentro de un coral");

        //Revisa las posiciones de los tiburones vivos
        for (Tiburon tiburon : tiburones) {

            String[] campos = estados.get(tiburon);

            if (Integer.parseInt(campos[2]) == 0) continue;

            int x = Integer.parseInt(campos[0]), y = Integer.parseInt(campos[1]);

            if (corales[x][y])
                throw new IOException("Tiburón dentro de un coral");

            if (ocupadas[x][y])
                throw new IOException("Dos tiburones en la misma casilla");

            ocupadas[x][y] = true;
        }
    }

    //Revisa que la cantidad guardada coincida con el nivel
    private void validarCantidad(Properties datos, String nombre, int cantidad) throws IOException {

        if (Integer.parseInt(datos.getProperty("cantidad" + nombre, "-1")) != cantidad)
            throw new IOException("La cantidad de elementos no coincide");
    }
}