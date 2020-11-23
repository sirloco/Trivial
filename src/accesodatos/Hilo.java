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

            System.out.println("Jugador conectado");

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

            //Se descifra la respuesta del jugador
            encriptador.init(Cipher.DECRYPT_MODE, secreta);

            //preparamos los flujos para la comunicacion con el jugador
            ObjectOutputStream oos = new ObjectOutputStream(sk.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(sk.getInputStream());

            //se envia la clave publica
            oos.writeObject(publica);

            //todo aqui empiezan los cambios

            String acertijo1 = "- Solo tiene una voz y anda con cuatro pies por la mañana, con dos pies al mediodía y " +
                    "con tres pies por la noche.";


            String nombre = "nombreJugador";
            String normasJuego = " -.-.-.-.-.- El juego de la Esfinge-.-.-.-.-.- \n Buenas tardes, " + nombre + " - dijo la Esfinge- " +
                    "\ntienes que adivinar mis tres acertijos si quieres entrar en la ciudad.";


            System.out.println(normasJuego);
            //oos.writeObject();
            /////////////////////////////////


            //Se recibe el mensaje cifrado
            byte[] respuesta = (byte[]) ois.readObject();

            //Se descifra el mensaje
            String respuestaDescifrada = new String(encriptador.doFinal(respuesta));

            System.out.println("respuesta del jugador: " + respuestaDescifrada);

            //Se cierran los flujos de comunicacion y el socket
            ois.close();
            oos.close();
            sk.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
