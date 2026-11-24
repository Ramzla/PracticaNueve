package com.example.practicas9;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Interfaz extends Application {
    private final int columnas = Main.COLUMNAS, filas = Main.FILAS, casilla = 48;
    private final Main juego = new Main();
    private Canvas tablero;
    private Label aviso;
    private final Label[] corazones = new Label[Main.MAX_VIDAS];
    private final ImageView[] iconosPerlas = new ImageView[3];
    private ImageView iconoBoya;
    private Tooltip ayudaBoya;
    private int vidasAnteriores = Main.MAX_VIDAS;

    private Image boya, buzo, botiquinImagen, submarino, coral, tiburonImagen, imagenJefe;
    private Image[] imagenesPerlas;
    private Stage ventanaPrincipal;
    private Timeline relojTiburones;
    private boolean relojEsJefe;
    private StackPane capaMenu;
    private StackPane capaNiveles;

    @Override
    public void start(Stage ventana) {
        ventanaPrincipal = ventana;
        juego.iniciarJuego();
        cargarImagenes();
        tablero = new Canvas(columnas * casilla, filas * casilla);

        StackPane areaTablero = new StackPane(tablero);
        capaMenu = crearMenu(ventana);
        capaNiveles = crearSeleccionNiveles(ventana);
        capaNiveles.setVisible(false);
        areaTablero.getChildren().addAll(capaMenu, capaNiveles);

        BorderPane pantalla = new BorderPane();
        pantalla.setCenter(areaTablero);
        pantalla.setRight(crearPanel());

        Scene escena = new Scene(pantalla);
        escena.setOnKeyPressed(e -> {
            if (capaMenu.isVisible() || capaNiveles.isVisible()) return;

            KeyCode tecla = e.getCode();
            switch (tecla) {
                case W -> { juego.moverJugador("norte"); actualizar(); }
                case S -> { juego.moverJugador("sur"); actualizar(); }
                case A -> { juego.moverJugador("oeste"); actualizar(); }
                case D -> { juego.moverJugador("este"); actualizar(); }
                case SPACE -> { juego.atacar(); actualizar(); }
                case F -> { juego.usarBotiquin(); actualizar(); }
                case R -> reiniciar();
                default -> { }
            }
        });

        ventana.setResizable(false);
        ventana.setScene(escena);
        ventana.show();
        actualizar();
        mostrarMenu(ventana);
    }

    private StackPane crearMenu(Stage ventana) {
        Label titulo = new Label("Rescate Marino");
        titulo.setFont(Font.font("Arial", 25));
        titulo.setStyle("-fx-text-fill: #135e87; -fx-font-weight: bold;");

        Label descripcion = new Label("Elige cómo quieres jugar");
        descripcion.setStyle("-fx-text-fill: #406a7d;");

        Button nueva = new Button("Nueva partida");
        Button cargar = new Button("Cargar partida");
        nueva.setPrefWidth(190);
        cargar.setPrefWidth(190);
        nueva.setStyle("-fx-background-color: #168bc4; -fx-text-fill: white; -fx-font-size: 14px;");
        cargar.setStyle("-fx-background-color: #d8ebf5; -fx-text-fill: #145b7e; -fx-font-size: 14px;");

        nueva.setOnAction(e -> mostrarSeleccionNiveles(ventana));

        cargar.setOnAction(e -> {
            juego.iniciarJuego();
            if (juego.cargarPartida()) {
                abrirJuego(ventana);
            } else {
                actualizar();
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.initOwner(ventana);
                alerta.setTitle("Cargar partida");
                alerta.setHeaderText("No se pudo cargar la partida");
                alerta.setContentText("Revisa que exista un archivo partida.txt válido en la carpeta del proyecto.");
                alerta.showAndWait();
            }
        });

        VBox menu = new VBox(16, titulo, descripcion, nueva, cargar);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(26));
        menu.setMaxWidth(320);
        menu.setMaxHeight(270);
        menu.setStyle("-fx-background-color: #eaf7ff; -fx-background-radius: 16; "
                + "-fx-border-color: #b4dceb; -fx-border-radius: 16; -fx-border-width: 2;");

        StackPane fondo = new StackPane(menu);
        fondo.setStyle("-fx-background-color: rgba(9, 50, 74, 0.55);");
        return fondo;
    }

    private StackPane crearSeleccionNiveles(Stage ventana) {
        Label titulo = new Label("Selecciona un nivel");
        titulo.setFont(Font.font("Arial", 26));
        titulo.setStyle("-fx-text-fill: #135e87; -fx-font-weight: bold;");

        Label descripcion = new Label("Solo el nivel 1 está disponible");
        descripcion.setStyle("-fx-text-fill: #406a7d; -fx-font-size: 13px;");

        ImageView imagenNivel1 = icono(cargarImagen("numero1"), 130);
        Label textoNivel1 = new Label("NIVEL 1");
        textoNivel1.setStyle("-fx-text-fill: #115c83; -fx-font-size: 17px; -fx-font-weight: bold;");
        Label disponible = new Label("Disponible");
        disponible.setStyle("-fx-text-fill: #168c77; -fx-font-size: 12px;");
        VBox contenidoNivel1 = new VBox(10, imagenNivel1, textoNivel1, disponible);
        contenidoNivel1.setAlignment(Pos.CENTER);

        Button nivel1 = new Button();
        nivel1.setGraphic(contenidoNivel1);
        nivel1.setPrefSize(185, 225);
        nivel1.setStyle("-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-border-color: #51b2ce; -fx-border-radius: 14; -fx-border-width: 2; -fx-cursor: hand;");
        nivel1.setOnAction(e -> {
            juego.iniciarJuego();
            abrirJuego(ventana);
        });

        ImageView imagenNivel2 = icono(cargarImagen("numero2"), 130);
        ColorAdjust apagado = new ColorAdjust();
        apagado.setSaturation(-0.75);
        apagado.setBrightness(0.18);
        imagenNivel2.setEffect(apagado);
        Label textoNivel2 = new Label("NIVEL 2");
        textoNivel2.setStyle("-fx-text-fill: #7a9baa; -fx-font-size: 17px; -fx-font-weight: bold;");
        Label bloqueado = new Label("Bloqueado - Próximamente");
        bloqueado.setStyle("-fx-text-fill: #8ca7b4; -fx-font-size: 12px;");
        VBox contenidoNivel2 = new VBox(10, imagenNivel2, textoNivel2, bloqueado);
        contenidoNivel2.setAlignment(Pos.CENTER);

        Button nivel2 = new Button();
        nivel2.setGraphic(contenidoNivel2);
        nivel2.setPrefSize(185, 225);
        nivel2.setStyle("-fx-background-color: #f2f7fa; -fx-background-radius: 14; "
                + "-fx-border-color: #d3e1e8; -fx-border-radius: 14; -fx-border-width: 2;");
        nivel2.setDisable(true);
        nivel2.setFocusTraversable(false);

        HBox opciones = new HBox(16, nivel1, nivel2);
        opciones.setAlignment(Pos.CENTER);

        Button volver = new Button("Volver al menú");
        volver.setStyle("-fx-background-color: transparent; -fx-text-fill: #246b91; "
                + "-fx-font-size: 13px; -fx-cursor: hand;");
        volver.setOnAction(e -> mostrarMenu(ventana));

        VBox contenido = new VBox(17, titulo, descripcion, opciones, volver);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(26));
        contenido.setMaxWidth(485);
        contenido.setMaxHeight(415);
        contenido.setStyle("-fx-background-color: #eaf7ff; -fx-background-radius: 18; "
                + "-fx-border-color: #b4dceb; -fx-border-radius: 18; -fx-border-width: 2;");

        StackPane fondo = new StackPane(contenido);
        fondo.setStyle("-fx-background-color: rgba(9, 50, 74, 0.55);");
        return fondo;
    }

    private void mostrarSeleccionNiveles(Stage ventana) {
        detenerMovimientoTiburones();
        capaMenu.setVisible(false);
        capaNiveles.setVisible(true);
        ventana.setTitle("Rescate Marino - Selección de niveles");
    }

    private void mostrarMenu(Stage ventana) {
        detenerMovimientoTiburones();
        capaNiveles.setVisible(false);
        capaMenu.setVisible(true);
        ventana.setTitle("Rescate Marino - Inicio");
    }

    private void abrirJuego(Stage ventana) {
        capaMenu.setVisible(false);
        capaNiveles.setVisible(false);
        ventana.setTitle("Práctica 10 - Rescate Marino");
        vidasAnteriores = juego.getJugador().getVida();
        actualizar();
        iniciarMovimientoTiburones();
    }

    private VBox crearPanel() {
        Label titulo = new Label("Rescate Marino");
        titulo.setFont(Font.font("Arial", 22));
        Label objetivo = new Label("Recoge 3 perlas, derrota al tiburón gigante y llega al submarino. La boya guarda tu progreso.");
        objetivo.setWrapText(true);

        HBox filaVidas = fila("Vidas:", 5);
        ((Label) filaVidas.getChildren().get(0)).setStyle("-fx-font-size: 16px;");
        for (int i = 0; i < corazones.length; i++) {
            Label corazon = new Label("♥");
            corazon.setFont(Font.font("Arial", 25));
            corazon.setMinWidth(23);
            corazones[i] = corazon;
            filaVidas.getChildren().add(corazon);
        }

        HBox filaPerlas = fila("Perlas:", 8);
        for (int i = 0; i < juego.getPerlas().length; i++) {
            ImageView icono = icono(imagenesPerlas[i], 37);
            icono.setFitHeight(35);
            Tooltip.install(icono, new Tooltip(juego.getPerlas()[i].getNombre()));
            iconosPerlas[i] = icono;
            filaPerlas.getChildren().add(icono);
        }

        HBox filaBoya = fila("Boya:", 8);
        iconoBoya = icono(boya, 32);
        iconoBoya.setFitHeight(38);
        ayudaBoya = new Tooltip();
        Tooltip.install(iconoBoya, ayudaBoya);
        filaBoya.getChildren().add(iconoBoya);

        Label tituloControles = new Label("Controles");
        tituloControles.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox controles = new VBox(4, tituloControles,
                control("teclas", 105, "Mover", 12),
                control("espacio", 110, "Atacar", 7),
                control("f", 48, "Curar", 12),
                control("r", 48, "Menú principal", 12));

        aviso = new Label();
        aviso.setWrapText(true);
        aviso.setMinHeight(48);
        aviso.setStyle("-fx-font-size: 13px; -fx-text-fill: #235f7b;");

        VBox panel = new VBox(7, titulo, objetivo, filaVidas, filaPerlas,
                filaBoya, controles, aviso);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(305);
        panel.setMinHeight(filas * casilla);
        panel.setStyle("-fx-background-color: #eaf7ff;");
        return panel;
    }

    private HBox fila(String texto, int separacion) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 15px;");
        HBox fila = new HBox(separacion, etiqueta);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private HBox control(String imagen, int ancho, String accion, int espacio) {
        HBox fila = new HBox(espacio, icono(cargarImagen(imagen), ancho), new Label(accion));
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private ImageView icono(Image imagen, int ancho) {
        ImageView vista = new ImageView(imagen);
        vista.setFitWidth(ancho);
        vista.setPreserveRatio(true);
        return vista;
    }

    private void reiniciar() {
        mostrarMenu(ventanaPrincipal);
    }

    private void iniciarMovimientoTiburones() {
        detenerMovimientoTiburones();
        relojEsJefe = juego.isJefeActivo();
        int intervalo = relojEsJefe ? Jefe.VELOCIDAD_MS : 550;
        relojTiburones = new Timeline(new KeyFrame(Duration.millis(intervalo), e -> {
            if (juego.isTermino()) {
                detenerMovimientoTiburones();
                return;
            }
            if (juego.isJefeActivo()) juego.moverJefe();
            else juego.moverTiburones();
            juego.revisarVida();
            actualizar();
            if (juego.isTermino()) detenerMovimientoTiburones();
        }));
        relojTiburones.setCycleCount(Animation.INDEFINITE);
        relojTiburones.play();
    }

    private void detenerMovimientoTiburones() {
        if (relojTiburones != null) {
            relojTiburones.stop();
            relojTiburones = null;
        }
    }

    @Override
    public void stop() {
        detenerMovimientoTiburones();
    }

    private void cargarImagenes() {
        boya = cargarImagen("boya");
        buzo = cargarImagen("buzo");
        botiquinImagen = cargarImagen("botiquin");
        submarino = cargarImagen("submarino");
        coral = cargarImagen("coral");
        tiburonImagen = cargarImagen("tiburon");
        imagenJefe = cargarImagen("jefe");
        imagenesPerlas = new Image[] {
                cargarImagen("perlaazul"), cargarImagen("perlaroja"), cargarImagen("perlaverde")
        };
    }

    private Image cargarImagen(String nombre) {
        return new Image(Interfaz.class.getResource("/imagenes/" + nombre + ".png").toExternalForm());
    }

    private void dibujar(GraphicsContext g, Image imagen, int x, int y, int ancho, int alto) {
        double px = x * casilla + (casilla - ancho) / 2.0;
        double py = y * casilla + (casilla - alto) / 2.0;
        g.drawImage(imagen, px, py, ancho, alto);
    }

    private void dibujarTablero(GraphicsContext g) {
        g.setFill(Color.web("#87d4ed"));
        g.fillRect(0, 0, tablero.getWidth(), tablero.getHeight());
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                Color color = x == 0 || y == 0 || x == columnas - 1 || y == filas - 1
                        ? Color.web("#167caa")
                        : (x + y) % 2 == 0 ? Color.web("#c5f0fb") : Color.web("#ade6f5");
                g.setFill(color);
                g.fillRect(x * casilla + 1, y * casilla + 1, casilla - 2, casilla - 2);
            }
        }
    }

    private void dibujarTiburon(GraphicsContext g, Personaje enemigo, int numero) {
        int x = enemigo.getPosicionX(), y = enemigo.getPosicionY();
        dibujar(g, tiburonImagen, x, y, 45, 23);
        g.setFill(Color.DARKRED);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30, 4);
        g.setFill(Color.LIMEGREEN);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30 * enemigo.getVida() / 50.0, 4);
        g.setFill(Color.BLACK);
        g.setFont(Font.font("Arial", 11));
        g.fillText(String.valueOf(numero), x * casilla + 22, y * casilla + 44);
    }

    private void actualizar() {
        if (tablero == null) return;
        if (relojTiburones != null && relojEsJefe != juego.isJefeActivo()) {
            iniciarMovimientoTiburones();
        }

        int vidas = juego.getJugador().getVida();
        for (int i = 0; i < corazones.length; i++) {
            Label corazon = corazones[i];
            corazon.setTextFill(i < vidas ? Color.web("#ce304c") : Color.web("#b1c7d1"));
            if (vidas != vidasAnteriores && i == (vidas < vidasAnteriores ? vidas : vidas - 1)) {
                ScaleTransition efecto = new ScaleTransition(Duration.millis(180), corazon);
                efecto.setFromX(1.6);
                efecto.setFromY(1.6);
                efecto.setToX(1);
                efecto.setToY(1);
                efecto.play();
            }
        }
        vidasAnteriores = vidas;

        for (int i = 0; i < iconosPerlas.length; i++) {
            iconosPerlas[i].setOpacity(juego.getRecogidas()[i] ? 1 : 0.25);
        }
        boolean guardado = juego.getNivel().getCheckPoints().get(0).isActivado();
        iconoBoya.setOpacity(guardado ? 1 : 0.25);
        ayudaBoya.setText(guardado
                ? "Progreso guardado. Vuelve a tocar la boya para sobrescribir partida.txt."
                : "Toca la boya para guardar el progreso en partida.txt.");
        aviso.setText(juego.getMensaje());

        GraphicsContext g = tablero.getGraphicsContext2D();
        dibujarTablero(g);
        dibujar(g, submarino, 10, 10, 45, 42);
        for (Obstaculo o : juego.getNivel().getObstaculos()) {
            if (!o.isDestruido()) dibujar(g, coral, o.getPosicionX(), o.getPosicionY(), 38, 43);
        }
        for (CheckPoint b : juego.getNivel().getCheckPoints()) {
            int x = b.getPosicionX(), y = b.getPosicionY();
            dibujar(g, boya, x, y, 31, 43);
            if (b.isActivado()) {
                g.setStroke(Color.LIMEGREEN);
                g.setLineWidth(3);
                g.strokeOval(x * casilla + 3, y * casilla + 3, casilla - 6, casilla - 6);
            }
        }
        for (int i = 0; i < juego.getPerlas().length; i++) {
            if (!juego.getRecogidas()[i]) {
                dibujar(g, imagenesPerlas[i], juego.getLugaresPerlas()[i][0], juego.getLugaresPerlas()[i][1], 41, 39);
            }
        }
        dibujar(g, botiquinImagen, juego.getBotiquin().getPosicionX(), juego.getBotiquin().getPosicionY(), 42, 37);
        if (juego.getBotiquin().isUsada()) {
            g.setFill(Color.rgb(70, 70, 70, 0.45));
            g.fillRect(juego.getBotiquin().getPosicionX() * casilla + 3,
                    juego.getBotiquin().getPosicionY() * casilla + 3, casilla - 6, casilla - 6);
        }
        if (juego.isJefeActivo()) {
            Jefe jefe = juego.getJefe();
            if (jefe.getVida() > 0) {
                int x = jefe.getPosicionX(), y = jefe.getPosicionY();
                dibujar(g, imagenJefe, x, y, 69, 61);
                g.setFill(Color.DARKRED);
                g.fillRect(x * casilla + 2, y * casilla - 2, 44, 5);
                g.setFill(Color.LIMEGREEN);
                g.fillRect(x * casilla + 2, y * casilla - 2,
                        44 * jefe.getVida() / (double) Jefe.VIDA_MAXIMA, 5);
            }
        } else {
            for (int i = 0; i < juego.getTiburones().length; i++) {
                if (juego.getTiburones()[i].getVida() > 0)
                    dibujarTiburon(g, juego.getTiburones()[i], i + 1);
            }
        }
        dibujar(g, buzo, juego.getJugador().getPosicionX(), juego.getJugador().getPosicionY(), 43, 40);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
