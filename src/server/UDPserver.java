
package server;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class UDPserver {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(null);

        socket.bind(new InetSocketAddress(1234));

        System.out.println("UDP Server listening on port 1234");

        // Liste des clients connectés
        Set<SocketAddress> clients = new HashSet<>();

        byte[] buffer = new byte[1024];

        while (true) {

            DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length);

            // Réception d'un message
            socket.receive(packet);

            // Adresse du client expéditeur
            SocketAddress clientAddress = packet.getSocketAddress();

            // Enregistrement du client s'il est nouveau
            if (clients.add(clientAddress)) {
                System.out.println("Nouveau client : " + clientAddress);
            }

            // Lecture du message reçu
            String msg = new String(
                    packet.getData(),
                    0,
                    packet.getLength(),
                    StandardCharsets.UTF_8
            );

            System.out.println(
                    "From " + clientAddress + " -> " + msg
            );

            // Préparation du message à diffuser
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);

            // Diffusion à tous les autres clients
            for (SocketAddress client : clients) {

                // Ne pas renvoyer le message à son expéditeur
                if (!client.equals(clientAddress)) {

                    DatagramPacket response =
                            new DatagramPacket(
                                    data,
                                    data.length,
                                    client
                            );

                    socket.send(response);
                }
            }
        }
    }
}
