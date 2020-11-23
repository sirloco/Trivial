package accesodatos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        System.out.println("Servidor en marcha");

        arrancaServer();
    }

    private static void arrancaServer() {

        try {

            //Puerto por el que se va a comunicar con el jugador
            int PUERTO = 5000;

            //Socket que va a recibir las respuestas del jugador
            ServerSocket skServer = new ServerSocket(PUERTO);

            //Socket del jugador
            Socket skJugador;

            while (true) {

                //Se aceptan pecitiones de varios jugadores
                skJugador = skServer.accept();

                //Se crea un hilo nuevo por cada jugador
                new Hilo(skJugador).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
