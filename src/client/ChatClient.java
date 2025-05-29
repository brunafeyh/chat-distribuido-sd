package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int TCP_PORT = 12345;
    private static final int UDP_PORT = 12346;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, TCP_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Thread para escutar mensagens do servidor
            new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada pelo servidor.");
                }
            }).start();

            // Interação do usuário
            while (true) {
                String input = scanner.nextLine();

                if (input.startsWith("/udp ")) {
                    String msg = input.substring(5);
                    sendUDPMessage(msg);
                    System.out.println("[UDP] Mensagem enviada.");
                } else {
                    out.println(input);

                    if (!input.trim().isEmpty()) {
                        System.out.println("[INFO] Você está conectado. Use /msg <usuário> <mensagem>, /all <mensagem> ou /list");
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }

    private static void sendUDPMessage(String message) {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(SERVER_HOST), UDP_PORT);
            udpSocket.send(packet);
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem UDP: " + e.getMessage());
        }
    }
}
