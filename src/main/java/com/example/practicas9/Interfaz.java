package com.example.practicas9;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Interfaz extends Application {
    // TAMANO_CASILLA - Tamaño de cada casilla
    private final int casilla = 48;
    private final Main juego = new Main();
    //Tablero del juego
    private Canvas tablero;
    //Imagenes del mapa
    private Image boya, buzo, botiquinImagen, submarino, coral, tiburonImagen;
    private final java.util.Map<String, Image> imagenesPerlas = new java.util.HashMap<>();
    private Stage ventanaPrincipal;
    //Reloj que llama a Main para mover tiburones
    private Timeline relojTiburones;
    private StackPane capaMenu;
    private StackPane capaNiveles;

    // INICIO_VENTANA - JavaFX abre la ventana
    @Override
    public void start(Stage ventana) {
        ventanaPrincipal = ventana;
        juego.iniciarJuego();
        cargarImagenes();
        tablero = new Canvas(juego.getNivel().getColumnas() * casilla, juego.getNivel().getFilas() * casilla);

        //Menus encima del tablero
        StackPane areaTablero = new StackPane(tablero);
        capaMenu = crearMenu(ventana);
        capaNiveles = crearSeleccionNiveles(ventana);
        capaNiveles.setVisible(false);
        areaTablero.getChildren().addAll(capaMenu, capaNiveles);

        // TECLAS - Llama a Main segun la tecla
        Scene escena = new Scene(areaTablero);
        escena.setOnKeyPressed(e -> {
            //Bloquea las teclas en los menus
            if (capaMenu.isVisible() || capaNiveles.isVisible()) return;

            KeyCode tecla = e.getCode();
            //Controles del buzo
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

        //Evita cambiar el tamaño de la ventana
        ventana.setResizable(false);
        ventana.setScene(escena);
        ventana.show();
        actualizar();
        mostrarMenu(ventana);
    }

    // ESTILOS_INTERFAZ - Estilos de los menus
    private Label etiqueta(String texto, String estilo) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle(estilo);
        return etiqueta;
    }

    private Label etiqueta(String texto, int tamano, String estilo) {
        Label etiqueta = etiqueta(texto, estilo);
        etiqueta.setFont(Font.font("Arial", tamano));
        return etiqueta;
    }

    private Button boton(String texto, int ancho, String estilo) {
        Button boton = new Button(texto);
        boton.setPrefWidth(ancho);
        boton.setStyle(estilo);
        return boton;
    }

    private Button tarjetaNivel(ImageView imagen, Label titulo, Label estado) {
        VBox contenido = new VBox(10, imagen, titulo, estado);
        contenido.setAlignment(Pos.CENTER);
        Button boton = new Button();
        boton.setGraphic(contenido);
        boton.setPrefSize(185, 225);
        return boton;
    }

    private VBox cajaMenu(int espacio, int ancho, int alto, javafx.scene.Node... elementos) {
        VBox caja = new VBox(espacio, elementos);
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(26));
        caja.setMaxSize(ancho, alto);
        return caja;
    }

    private StackPane fondoMenu(VBox contenido) {
        StackPane fondo = new StackPane(contenido);
        fondo.setStyle("-fx-background-color: rgba(9, 50, 74, 0.55);");
        return fondo;
    }

    // MENU_PRINCIPAL - Nueva partida o cargar
    private StackPane crearMenu(Stage ventana) {
        Label titulo = etiqueta("Rescate Marino", 25, "-fx-text-fill: #135e87; -fx-font-weight: bold;");

        Label descripcion = etiqueta("Elige cómo quieres jugar", "-fx-text-fill: #406a7d;");

        //Estilo de los botones
        Button nueva = boton("Nueva partida", 190, "-fx-background-color: #168bc4; -fx-text-fill: white; -fx-font-size: 14px;");
        Button cargar = boton("Cargar partida", 190, "-fx-background-color: #d8ebf5; -fx-text-fill: #145b7e; -fx-font-size: 14px;");

        //Abre la seleccion de niveles
        nueva.setOnAction(e -> mostrarSeleccionNiveles(ventana));

        // CARGAR_PARTIDA - Main intenta cargar el progreso
        cargar.setOnAction(e -> {
            juego.iniciarJuego();
            if (juego.cargarPartida()) {
                abrirJuego(ventana);
            } else {
                actualizar();
                //Aviso si falla la carga
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.initOwner(ventana);
                alerta.setTitle("Cargar partida");
                alerta.setHeaderText("No se pudo cargar la partida");
                alerta.setContentText("Revisa que exista un archivo partida.txt válido en la carpeta del proyecto.");
                alerta.showAndWait();
            }
        });

        //Menu con separacion de 16
        VBox menu = cajaMenu(16, 320, 270, titulo, descripcion, nueva, cargar);
        menu.setStyle("-fx-background-color: #eaf7ff; -fx-background-radius: 16; "
                + "-fx-border-color: #b4dceb; -fx-border-radius: 16; -fx-border-width: 2;");

        //Fondo del menu
        return fondoMenu(menu);
    }

    // SELECCION_NIVELES - Menu de niveles
    private StackPane crearSeleccionNiveles(Stage ventana) {
        Label titulo = etiqueta("Selecciona un nivel", 26, "-fx-text-fill: #135e87; -fx-font-weight: bold;");

        Label descripcion = etiqueta("Solo el nivel 1 está disponible", "-fx-text-fill: #406a7d; -fx-font-size: 13px;");

        // NIVEL_1 - Main inicia la partida
        ImageView imagenNivel1 = icono(cargarImagen("numero1"), 130);
        Label textoNivel1 = etiqueta("NIVEL 1", "-fx-text-fill: #115c83; -fx-font-size: 17px; -fx-font-weight: bold;");
        Label disponible = etiqueta("Disponible", "-fx-text-fill: #168c77; -fx-font-size: 12px;");
        Button nivel1 = tarjetaNivel(imagenNivel1, textoNivel1, disponible);
        nivel1.setStyle("-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-border-color: #51b2ce; -fx-border-radius: 14; -fx-border-width: 2; -fx-cursor: hand;");
        nivel1.setOnAction(e -> {
            juego.iniciarJuego();
            abrirJuego(ventana);
        });

        // NIVEL_2 - Nivel bloqueado
        ImageView imagenNivel2 = icono(cargarImagen("numero2"), 130);
        ColorAdjust apagado = new ColorAdjust();
        //Cambia el color de la imagen
        apagado.setSaturation(-0.75);
        apagado.setBrightness(0.18);
        imagenNivel2.setEffect(apagado);
        Label textoNivel2 = etiqueta("NIVEL 2", "-fx-text-fill: #7a9baa; -fx-font-size: 17px; -fx-font-weight: bold;");
        Label bloqueado = etiqueta("Bloqueado - Próximamente", "-fx-text-fill: #8ca7b4; -fx-font-size: 12px;");
        Button nivel2 = tarjetaNivel(imagenNivel2, textoNivel2, bloqueado);
        nivel2.setStyle("-fx-background-color: #f2f7fa; -fx-background-radius: 14; "
                + "-fx-border-color: #d3e1e8; -fx-border-radius: 14; -fx-border-width: 2;");
        //Nivel 2 aun no programado
        nivel2.setDisable(true);
        nivel2.setFocusTraversable(false);

        //Niveles uno al lado del otro
        HBox opciones = new HBox(16, nivel1, nivel2);
        opciones.setAlignment(Pos.CENTER);

        //Regresa al menu
        Button volver = new Button("Volver al menú");
        volver.setStyle("-fx-background-color: transparent; -fx-text-fill: #246b91; "
                + "-fx-font-size: 13px; -fx-cursor: hand;");
        volver.setOnAction(e -> mostrarMenu(ventana));

        VBox contenido = cajaMenu(17, 485, 415, titulo, descripcion, opciones, volver);
        contenido.setStyle("-fx-background-color: #eaf7ff; -fx-background-radius: 18; "
                + "-fx-border-color: #b4dceb; -fx-border-radius: 18; -fx-border-width: 2;");

        return fondoMenu(contenido);
    }

    //Abre los niveles y pausa los tiburones
    private void mostrarSeleccionNiveles(Stage ventana) {
        detenerMovimientoTiburones();
        capaMenu.setVisible(false);
        capaNiveles.setVisible(true);
        ventana.setTitle("Rescate Marino - Selección de niveles");
    }

    //Abre el menu y pausa los tiburones
    private void mostrarMenu(Stage ventana) {
        detenerMovimientoTiburones();
        capaNiveles.setVisible(false);
        capaMenu.setVisible(true);
        ventana.setTitle("Rescate Marino - Inicio");
    }

    //Oculta los menus y activa los tiburones
    private void abrirJuego(Stage ventana) {
        capaMenu.setVisible(false);
        capaNiveles.setVisible(false);
        actualizar();
        iniciarMovimientoTiburones();
    }

    //Ajusta el ancho de la imagen
    private ImageView icono(Image imagen, int ancho) {
        ImageView vista = new ImageView(imagen);
        vista.setFitWidth(ancho);
        //Evita deformar la imagen
        vista.setPreserveRatio(true);
        return vista;
    }

    //R regresa al menu
    private void reiniciar() {
        mostrarMenu(ventanaPrincipal);
    }

    // VELOCIDAD_TIBURONES - Main indica el intervalo
    private void iniciarMovimientoTiburones() {
        detenerMovimientoTiburones();
        int intervalo = juego.getVelocidadTiburones();
        //Ejecuta un turno cada intervalo
        relojTiburones = new Timeline(new KeyFrame(Duration.millis(intervalo), e -> {
            //Detiene el reloj si termina el juego
            if (juego.isTermino()) {
                detenerMovimientoTiburones();
                return;
            }
            juego.moverTiburones();
            //Dibuja el mapa despues del turno
            actualizar();
            if (juego.isTermino()) detenerMovimientoTiburones();
        }));
        //Repite los turnos
        relojTiburones.setCycleCount(Animation.INDEFINITE);
        relojTiburones.play();
    }

    //Detiene el reloj
    private void detenerMovimientoTiburones() {
        if (relojTiburones != null) {
            relojTiburones.stop();
            relojTiburones = null;
        }
    }

    //JavaFX detiene el reloj al cerrar
    @Override
    public void stop() {
        detenerMovimientoTiburones();
    }

    // CARGAR_IMAGENES - Carga las imagenes del juego
    private void cargarImagenes() {
        boya = cargarImagen("boya");
        buzo = cargarImagen("buzo");
        botiquinImagen = cargarImagen("botiquin");
        submarino = cargarImagen("submarino");
        coral = cargarImagen("coral");
        tiburonImagen = cargarImagen("tiburon");
        for (Recompensa perla : juego.getNivel().getPerlas())
            imagenesPerlas.computeIfAbsent(perla.getImagen(), this::cargarImagen);
    }

    //Busca la imagen en resources/imagenes
    private Image cargarImagen(String nombre) {
        return new Image(Interfaz.class.getResource("/imagenes/" + nombre + ".png").toExternalForm());
    }

    // DIBUJAR_IMAGEN - Centra la imagen en su casilla
    private void dibujar(GraphicsContext g, Image imagen, int x, int y, int ancho, int alto) {
        //Convierte casillas a pixeles
        double px = x * casilla + (casilla - ancho) / 2.0;
        double py = y * casilla + (casilla - alto) / 2.0;
        g.drawImage(imagen, px, py, ancho, alto);
    }

    // COLORES_MAPA - Colores de las casillas
    private void dibujarTablero(GraphicsContext g) {
        //Fondo del tablero
        g.setFill(Color.web("#87d4ed"));
        g.fillRect(0, 0, tablero.getWidth(), tablero.getHeight());
        int columnas = juego.getNivel().getColumnas(), filas = juego.getNivel().getFilas();
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                //Bordes oscuros y colores alternados
                Color color = x == 0 || y == 0 || x == columnas - 1 || y == filas - 1
                        ? Color.web("#167caa")
                        : (x + y) % 2 == 0 ? Color.web("#c5f0fb") : Color.web("#ade6f5");
                g.setFill(color);
                //Separa las casillas
                g.fillRect(x * casilla + 1, y * casilla + 1, casilla - 2, casilla - 2);
            }
        }
    }

    // TAMANO_TIBURON - Dibuja al tiburon
    private void dibujarTiburon(GraphicsContext g, Tiburon enemigo, int numero) {
        int x = enemigo.getPosicionX(), y = enemigo.getPosicionY();
        //Ancho y alto del tiburon
        dibujar(g, tiburonImagen, x, y, 45, 23);
        // BARRA_VIDA - Fondo rojo de la barra
        g.setFill(Color.DARKRED);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30, 4);
        //Parte verde segun la vida actual
        g.setFill(Color.LIMEGREEN);
        g.fillRect(x * casilla + 9, y * casilla + 5, 30 * enemigo.getVida() / (double) enemigo.getVidaMaxima(), 4);
        g.setFill(Color.BLACK);
        g.setFont(Font.font("Arial", 11));
        g.fillText(String.valueOf(numero), x * casilla + 22, y * casilla + 44);
    }

    // ACTUALIZAR - Consulta Nivel y dibuja el mapa
    private void actualizar() {
        if (tablero == null) return;
        Nivel nivel = juego.getNivel();

        // ESTADO_PARTIDA - Vidas, perlas y boya en el titulo
        if (!capaMenu.isVisible() && !capaNiveles.isVisible()) {
            ventanaPrincipal.setTitle("Rescate Marino | Vidas: " + nivel.getBuzo().getVida()
                    + "/" + nivel.getBuzo().getVidaMaxima() + " | Perlas: "
                    + nivel.getCantidadPerlas() + "/" + nivel.getTotalPerlas()
                    + " | Boya: " + (nivel.getBoya().isActivado() ? "Activa" : "Inactiva"));
        }
        Recompensa[] perlas = nivel.getPerlas();

        GraphicsContext g = tablero.getGraphicsContext2D();
        dibujarTablero(g);
        // SUBMARINO - Posicion desde Nivel
        dibujar(g, submarino, nivel.getSalidaX(), nivel.getSalidaY(), 45, 42);
        // CORALES - Dibuja los corales sin destruir
        for (Obstaculo o : nivel.getObstaculos()) {
            if (!o.isDestruido()) dibujar(g, coral, o.getPosicionX(), o.getPosicionY(), 38, 43);
        }
        // BOYA_MAPA - Dibuja la boya de Nivel
        CheckPoint b = nivel.getBoya();
        int x = b.getPosicionX(), y = b.getPosicionY();
        dibujar(g, boya, x, y, 31, 43);
        //Circulo verde si esta activada
        if (b.isActivado()) {
            g.setStroke(Color.LIMEGREEN);
            g.setLineWidth(3);
            g.strokeOval(x * casilla + 3, y * casilla + 3, casilla - 6, casilla - 6);
        }
        // PERLAS_MAPA - Dibuja las perlas no recogidas
        for (Recompensa perla : perlas) {
            if (!perla.isRecogida())
                dibujar(g, imagenesPerlas.get(perla.getImagen()), perla.getPosicionX(), perla.getPosicionY(), 41, 39);
        }
        // BOTIQUIN_MAPA - Dibuja el botiquin
        dibujar(g, botiquinImagen, nivel.getBotiquin().getPosicionX(), nivel.getBotiquin().getPosicionY(), 42, 37);
        //Oscurece el botiquin usado
        if (nivel.getBotiquin().isUsada()) {
            g.setFill(Color.rgb(70, 70, 70, 0.45));
            g.fillRect(nivel.getBotiquin().getPosicionX() * casilla + 3,
                    nivel.getBotiquin().getPosicionY() * casilla + 3, casilla - 6, casilla - 6);
        }
        // TIBURONES_MAPA - Dibuja los tiburones vivos
        Tiburon[] tiburones = nivel.getTiburones();
        for (int i = 0; i < tiburones.length; i++) {
            if (tiburones[i].getVida() > 0)
                dibujarTiburon(g, tiburones[i], i + 1);
        }
        // BUZO_MAPA - Dibuja al buzo
        dibujar(g, buzo, nivel.getBuzo().getPosicionX(), nivel.getBuzo().getPosicionY(), 43, 40);
        // MENSAJES_MAPA - Controles y mensajes de Main
        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", 13));
        g.fillText("WASD: mover | Espacio: atacar | F: curar | R: menú", 10, 28, tablero.getWidth() - 20);
        g.fillText(juego.getMensaje(), 10, tablero.getHeight() - 19, tablero.getWidth() - 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
