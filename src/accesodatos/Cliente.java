package accesodatos;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class Cliente {

    public static void main(String[] args) {
        enviarRespuesta();
    }

    private static void enviarRespuesta() {

        //Se prepara el flujo de entrada por teclado
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        //Se establece el mismo puerto de comunicacion que en el servidor
        final int PUERTO = 5000;

        try {

            //Se crea el socket de comunicacion con el servidor
            Socket cliente = new Socket("localhost", PUERTO);

            //Se crean los flujos de comunicaion con el servidor
            ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());

            //SE  recoge la clave publica
            PublicKey llave = (PublicKey) ois.readObject();

            System.out.println("Clave publica Recibida " + llave);

            //Se crea el encriptador para descifrar/cifrar los mensajes
            Cipher encriptador = Cipher.getInstance("RSA");

            //Encriptando la respuesta para enviar al servidor
            encriptador.init(Cipher.ENCRYPT_MODE,llave);

            //Se obtiene por teclado la respuesta del jugador
            System.out.print("Respuesta: ");
            String respuesta = br.readLine();

            //se encripta la respuesta para ser enviada
            byte[] respuestaCifrada = encriptador.doFinal(respuesta.getBytes());

            //Se envia la respuesta
            oos.writeObject(respuestaCifrada);

            //Se cierran los flujos de comunicacion y el socket
            ois.close();
            oos.close();
            cliente.close();


        } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }
}
