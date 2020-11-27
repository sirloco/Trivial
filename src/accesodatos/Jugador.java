package accesodatos;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;

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

      /****************************************************************************************************************/
      // Se envia el nombre al servidor //
      /****************************************************************************************************************/

      boolean continua;
      do {

        System.out.print("Nombre: ");
        String nombre = br.readLine();

        flujosalida.println(nombre);

        continua = flujoentrada.readLine().equals("mal");

        System.out.println(continua ?
          "El nombre debe tener al menos cinco letras y minusculas" : "Ok");

      } while (continua);

      /****************************************************************************************************************/
      // Se envian los apellidos al servidor //
      /****************************************************************************************************************/

      do {

        System.out.print("Apellidos: ");
        String apellidos = br.readLine();

        flujosalida.println(apellidos);

        continua = flujoentrada.readLine().equals("mal");

        System.out.println(continua ?
          "Los apellidos debe tener al menos cinco letras y minusculas" : "Ok");

      } while (continua);

      /****************************************************************************************************************/
      // Se envia la edad al servidor //
      /****************************************************************************************************************/

      do {

        System.out.print("Edad: ");
        String edad = br.readLine();

        flujosalida.println(edad);

        continua = flujoentrada.readLine().equals("mal");

        System.out.println(continua ? "Debe tener entra 18 y 90 años" : "Ok");

      } while (continua);

      /****************************************************************************************************************/
      // Se envia el nick al servidor //
      /****************************************************************************************************************/

      do {

        System.out.print("Nick: ");
        String nick = br.readLine();

        flujosalida.println(nick);

        continua = flujoentrada.readLine().equals("mal");

        System.out.println(continua ? "Debe tener al menos un caractery minusculas" : "Ok");

      } while (continua);

      /****************************************************************************************************************/
      // Se envia la contraseña al servidor //
      /****************************************************************************************************************/

      do {

        System.out.print("Contraseña: ");
        String contrasinal = br.readLine();

        flujosalida.println(contrasinal);

        continua = flujoentrada.readLine().equals("mal");

        System.out.println(continua ? "Debe tener al menos una letra minuscula otra mayuscula un numero un " +
          "caracter especial y longitud de 10 caracteres" : "Ok");

      } while (continua);

      /****************************************************************************************************************/
      // comprobacion de la firma //
      /****************************************************************************************************************/

      //Se indica el tipo de firma recibida
      Signature verificarsa = Signature.getInstance("SHA1WITHRSA");

      //Se indica la clave publica del servidor
      verificarsa.initVerify(llave);

      //Se reciben las normas sin firmar
      String normas = ois.readObject().toString();

      System.out.println(normas);

      //Se preparan para ser verificadas
      verificarsa.update(normas.getBytes());

      //Se obtiene la firma de esas normas
      byte[] firma = (byte[]) ois.readObject();

      System.out.print("Verificando que la Esfinge es el emisor....");

      //se verifica la firma
      System.out.print(verificarsa.verify(firma) ? "Verificada firma de las normas del juego" : "Intento de falsificación");

      /****************************************************************************************************************/

      System.out.println();

      //Regogemos el acertijo
      System.out.println(flujoentrada.readLine());

      System.out.println();

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

    } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | SignatureException e) {
      e.printStackTrace();
    }

  }
}
