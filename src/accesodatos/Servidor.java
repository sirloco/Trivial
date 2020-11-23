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

            //Puerto por el que se va a comunicar con el cliente
            int PUERTO = 5000;

            //Socket que va a recibir las peticiones del cliente
            ServerSocket skServer = new ServerSocket(PUERTO);

            //Socket del cliente
            Socket skCliente;

            while (true) {

                //Se aceptan pecitiones de varios clientes
                skCliente = skServer.accept();

                //Se crea un hilo nuevo por cada peticion
                new Hilo(skCliente).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
