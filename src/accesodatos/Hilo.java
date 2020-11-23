package accesodatos;

import javax.crypto.Cipher;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Hilo extends Thread {

    Socket sk;

    public Hilo(Socket sk) {
        this.sk = sk;
    }

    @Override
    public void run() {

        try {

            System.out.println("Cliente conectado");

            //Se indica el tipo de cifrado
            KeyPairGenerator llaves = KeyPairGenerator.getInstance("RSA");

            //Tamaño de la clave
            llaves.initialize(1024);

            //Se generan las llaves
            KeyPair keys = llaves.generateKeyPair();

            //Se saca la publica y la privada
            PublicKey publica = keys.getPublic();
            PrivateKey secreta = keys.getPrivate();

            //Se crea el encriptador para descifrar/cifrar la comunicacion
            Cipher encriptador = Cipher.getInstance("RSA");

            //Se descifra el mensaje del cliente
            encriptador.init(Cipher.DECRYPT_MODE,secreta);

            //preparamos los flujos para la comunicacion con el cliente
            ObjectOutputStream oos = new ObjectOutputStream(sk.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sk.getInputStream());

            //se envia la clave publica
            oos.writeObject(publica);

            //Se recibe el mensaje cifrado
            byte[] mensaje = (byte[]) ois.readObject();

            //Se descifra el mensaje
            String mensajeDescifrado = new String(encriptador.doFinal(mensaje));

            System.out.println("mensaje del cliente: " + mensajeDescifrado);

            ois.close();
            oos.close();
            sk.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
