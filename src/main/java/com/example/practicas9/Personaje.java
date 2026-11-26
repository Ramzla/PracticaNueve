package com.example.practicas9;

//Caracteristicas del buzo y los tiburones
public abstract class Personaje extends EntidadMapa implements Destruible, ElementoDinamico {

    private int vida;
    private final int vidaMaxima;

    //Inicializa la vida y posicion del personaje
    protected Personaje(String nombre, int vida, int posicionX, int posicionY) {
        super(nombre, posicionX, posicionY);
        this.vidaMaxima = Math.max(0, vida);
        this.vida = vidaMaxima;
    }

    //Interfaz usa las vidas para mostrarlas en pantalla
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }

    //Se usa en los ataques del buzo y los tiburones
    public void recibirDaño(int daño) {
        if (daño < 0) throw new IllegalArgumentException("El daño no puede ser negativo");
        vida = Math.max(0, vida - daño);
        if (vida == 0) destruye();
    }

    //Utileria recupera la vida sin superar el maximo
    public void reparar(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad inválida");
        vida = (int) Math.min(vidaMaxima, (long) vida + cantidad);
    }

    //Nivel guarda la posicion y vida
    @Override
    public String guardarEstado() { return super.guardarEstado() + "," + vida; }

    //Se guardan X, Y y vida
    @Override
    protected int cantidadCampos() { return 3; }

    //Nivel revisa que la vida guardada sea valida
    @Override
    public void validarEstado(String[] campos) throws java.io.IOException {
        super.validarEstado(campos);
        int guardada = Integer.parseInt(campos[2]);
        if (guardada < 0 || guardada > vidaMaxima) throw new java.io.IOException("Vida inválida: " + getNombre());
    }

    //Nivel recupera la posicion y vida guardada
    @Override
    public void restaurarEstado(String[] campos) {
        super.restaurarEstado(campos);
        vida = Integer.parseInt(campos[2]);
    }

    //Main usa este metodo para mover al buzo
    public boolean mover(Nivel nivel, String direccion) {
        int[] nuevo = siguienteLugar(direccion);
        if (!nivel.lugarLibre(nuevo[0], nuevo[1])) return false;

        mover(direccion, 1);
        return true;
    }

    //Desplaza al personaje
    @Override
    public void mover(String direccion, int distancia) { desplazar(direccion, distancia); }

    //Deja al personaje sin vida
    @Override
    public void destruye() { vida = 0; }

    //Muestra los datos del personaje como texto
    @Override
    public String toString() {
        return getNombre() + " (" + getPosicionX() + "," + getPosicionY() + ") [vida=" + vida + "]";
    }
}