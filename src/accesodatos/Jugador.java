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

public class Jugador {

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
            PrintWriter flujosalida = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader flujoentrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            //SE  recoge la clave publica
            PublicKey llave = (PublicKey) ois.readObject();

            System.out.println("Clave publica Recibida " + llave);


            /********************************* Se envia el nombre al servidor *****************************************/

            boolean continua;
            do {

                System.out.println("Nombre Jugador: ");
                String nombre = br.readLine();

                flujosalida.println(nombre);

                continua = flujoentrada.readLine().equals("mal");

                System.out.println(continua ?
                        "El nombre debe tener al menos cinco letras y minusculas" : "Nombre Correcto");

            } while (continua);

            /**********************************************************************************************************/


            /********************************* Se envian los apellidos al servidor ************************************/

            do {

                System.out.println("Apellidos Jugador: ");
                String apellidos = br.readLine();

                flujosalida.println(apellidos);

                continua = flujoentrada.readLine().equals("mal");

                System.out.println(continua ?
                        "Los apellidos debe tener al menos cinco letras y minusculas" : "Apellidos Correctos");

            } while (continua);

            /**********************************************************************************************************/

            /********************************* Se envia la edad al servidor *******************************************/

            do {

                System.out.println("Edad del Jugador: ");
                String edad = br.readLine();

                flujosalida.println(edad);

                continua = flujoentrada.readLine().equals("mal");

                System.out.println(continua ? "Debe ser mayor de edad" : "Edad Correcta");

            } while (continua);

            /**********************************************************************************************************/


            //Se crea el encriptador para descifrar/cifrar los mensajes
            Cipher encriptador = Cipher.getInstance("RSA");

            //Encriptando la respuesta para enviar al servidor
            encriptador.init(Cipher.ENCRYPT_MODE, llave);

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
