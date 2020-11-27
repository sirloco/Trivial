package accesodatos;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hilo extends Thread {

    Socket sk;

    Map<String, String> datosJugador = new HashMap<>();

    int puntos = 0;

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
            PrintWriter flujosalida = new PrintWriter(sk.getOutputStream(), true);
            BufferedReader flujoentrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            //se envia la clave publica
            oos.writeObject(publica);

            //********************************************************************************************************//
            // comprobamos Nombre del jugador //
            //********************************************************************************************************//

            Matcher mat;
            Pattern pat;
            do {

                //recibimos el nombre del jugador
                datosJugador.put("nombre", flujoentrada.readLine());

                //Patron de letras en minusculas de 5 caracteres o mas
                pat = Pattern.compile("[a-z]{5,}");

                //Se hace la comprobacion
                mat = pat.matcher(datosJugador.get("nombre"));

                //Devuelve al jugador bien o mal en funcion de lo recibido
                flujosalida.println(mat.matches() ? "bien" : "mal");

            } while (!mat.matches());//Repite si no cumple el patron

            //********************************************************************************************************//
            // comprobamos Apellidos del jugador //
            //********************************************************************************************************//

            do {

                //recibimos los apellidos del jugador
                datosJugador.put("apellidos", flujoentrada.readLine());

                //Patron de letras en minusculas de 5 caracteres o mas
                pat = Pattern.compile("[a-z]{5,}");

                //Se hace la comprobacion
                mat = pat.matcher(datosJugador.get("apellidos"));

                //Devuelve al jugador bien o mal en funcion de lo recibido
                flujosalida.println(mat.matches() ? "bien" : "mal");

            } while (!mat.matches());//Repite si no cumple el patron

            //********************************************************************************************************//
            // comprobamos edad del jugador //
            //********************************************************************************************************//

            do {

                //recibimos la edad del jugador
                datosJugador.put("edad", flujoentrada.readLine());

                //Patron mayor de 18 años menor
                pat = Pattern.compile("^1[8-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9]|6[0-9]|7[0-9]|8[0-9]|9[0-9]$");

                //Se hace la comprobacion
                mat = pat.matcher(datosJugador.get("edad"));

                //Devuelve al jugador bien o mal en funcion de lo recibido
                flujosalida.println(mat.matches() ? "bien" : "mal");

            } while (!mat.matches());//Repite si no cumple el patron

            //********************************************************************************************************//
            // comprobamos el nick del jugador //
            //********************************************************************************************************//

            do {

                //recibimos la edad del jugador
                datosJugador.put("nick", flujoentrada.readLine());

                //Patron mayor de 18 años menor
                pat = Pattern.compile("[a-z]{2,}");

                //Se hace la comprobacion
                mat = pat.matcher(datosJugador.get("nick"));

                //Devuelve al jugador bien o mal en funcion de lo recibido
                flujosalida.println(mat.matches() ? "bien" : "mal");

            } while (!mat.matches());//Repite si no cumple el patron

            //********************************************************************************************************//
            // comprobamos la contraseña del jugador //
            //********************************************************************************************************//

            do {

                //recibimos la edad del jugador
                datosJugador.put("contraseña", flujoentrada.readLine());

                //Patron mayor de 18 años menor
                pat = Pattern.compile("^(?=.{10,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\W).*$");

                //Se hace la comprobacion
                mat = pat.matcher(datosJugador.get("contraseña"));

                //Devuelve al jugador bien o mal en funcion de lo recibido
                flujosalida.println(mat.matches() ? "bien" : "mal");

            } while (!mat.matches());//Repite si no cumple el patron

            //********************************************************************************************************//
            // FIRMANDO NORMAS DEL JUEGO //
            //********************************************************************************************************//

            //se crea el tipo de firma que se va a utilizar
            Signature rsa = Signature.getInstance("SHA1WITHRSA");

            //indicamos la clave privada que va a utilizar para la firma
            rsa.initSign(secreta);

            String normasJuego = " -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.- El juego de la Esfinge -.-.-.-.-.-.-.-.-.- \n Buenas tardes," +
                    datosJugador.get("nombre") + " - dijo la Esfinge- " +
                    "\ntienes que adivinar mis tres acertijos si quieres entrar en la ciudad. de no ser así morirás." +
                    "\n-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-\n";

            //Se prepara el mensaje para firmar
            rsa.update(normasJuego.getBytes());

            //se firma el mensaje
            byte[] firma = rsa.sign();

            //se envia las normas sin firmar
            oos.writeObject(normasJuego);

            //Se envian firmadas las normas
            oos.writeObject(firma);
            
            //********************************************************************************************************//
            //********************************************************************************************************//


            //PRIMER ACERTIJO
            flujosalida.println("- Solo tiene una voz y anda con cuatro pies por la mañana, con dos pies al mediodía " +
                    "y con tres pies por la noche.");

            recepcionAcertijo(ois, encriptador, flujosalida,"humano");

            //SEGUNDO ACERTIJO
            flujosalida.println("- Oro parece plata no es el que no lo sepa un tonto es.");
            recepcionAcertijo(ois, encriptador, flujosalida,"platano");



            //TERCER ACERTIJO
            flujosalida.println("- En alto vive, en alto mora, en alto teje, la tejedora.");
            recepcionAcertijo(ois, encriptador, flujosalida,"araña");

            flujosalida.println(

                    puntos < 30 ?

                            "Por desgracia no has conseguido acertar todos mis acertijos " + datosJugador.get("nombre") +
                                    " ( " + datosJugador.get("nick") + " ) has conseguido " + puntos + " puntos y aun asi debes morir."

                            : "Has conseguido acertar todos mis acertijos " + datosJugador.get("nombre") +
                            " ( " + datosJugador.get("nick") + ") has conseguido " + puntos + " puntos y por ello " +
                            "puedes entrar en la ciudad."
            );


            //Se cierran los flujos de comunicacion y el socket
            ois.close();
            oos.close();
            sk.close();

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private void recepcionAcertijo(ObjectInputStream ois, Cipher encriptador, PrintWriter flujosalida,String solucion) {

        try {

            //Se recibe el mensaje cifrado
            byte[] respuesta = (byte[]) ois.readObject();

            //Se descifra el mensaje
            String respuestaDescifrada = new String(encriptador.doFinal(respuesta));

            if (respuestaDescifrada.contains(solucion)) {
                puntos += 10;
                flujosalida.println("Enorabuena has acertado!, Recompensa +10 Puntos");
            } else {
                flujosalida.println("Respuesta incorrecta, Recompensa 0 Puntos");
            }

            System.out.println("respuesta del jugador: " + respuestaDescifrada);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
