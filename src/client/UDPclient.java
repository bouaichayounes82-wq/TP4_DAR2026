
package client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPclient {

    public static void main(String[] args) throws Exception {

        String host = args.length > 0 ? args[0] : "localhost";
        int port = 1234;

        BufferedReader keyboard =
                new BufferedReader(
                        new InputStreamReader(System.in)
                );

        System.out.print("Username: ");
        String name = keyboard.readLine();

        DatagramSocket socket = new DatagramSocket();

        InetSocketAddress server =
                new InetSocketAddress(
                        InetAddress.getByName(host),
                        port
                );

        // Thread pour recevoir les messages
        Thread receiver = new Thread(() -> {

            byte[] buffer = new byte[1024];

            try {

                while (!socket.isClosed()) {

                    DatagramPacket packet =
                            new DatagramPacket(
                                    buffer,
                                    buffer.length
                            );

                    // Attendre un message du serveur
                    socket.receive(packet);

                    String msg = new String(
                            packet.getData(),
                            0,
                            packet.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.println("\n" + msg);
                    System.out.print("Message : ");
                }

            } catch (SocketException e) {

                // Le socket a été fermé
                System.out.println("Réception terminée.");

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        // Démarrer le thread de réception
        receiver.start();

        // Boucle principale pour envoyer les messages
        String line;

        while ((line = keyboard.readLine()) != null) {

            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            byte[] data =
                    ("[" + name + "] : " + line)
                    .getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet =
                    new DatagramPacket(
                            data,
                            data.length,
                            server
                    );

            // Envoyer le message au serveur
            socket.send(packet);
        }

        // Fermer le socket
        socket.close();

        System.out.println("Client déconnecté.");
    }
}
