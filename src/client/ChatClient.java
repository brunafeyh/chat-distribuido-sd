package client;

import common.EncryptionUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int TCP_PORT = 12345;
    private static final int UDP_PORT = 12346;
    private static final SecretKey encryptionKey;

    static {
        try {
            encryptionKey = EncryptionUtil.getEncryptionKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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
                        String descryptMessage = EncryptionUtil.decrypt(serverMsg, encryptionKey);
                        System.out.println(descryptMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada pelo servidor.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

            // Interação do usuário
            while (true) {
                String input = scanner.nextLine();

                if (input.startsWith("/udp ")) {
                    String crypt = EncryptionUtil.encrypt(input, encryptionKey);
                    String msg = crypt.substring(5);
                    sendUDPMessage(msg);
                    System.out.println("[UDP] Mensagem enviada.");
                } else {
                    String crypt = EncryptionUtil.encrypt(input, encryptionKey);

                    System.out.println(crypt);
                    System.out.println(encryptionKey);
                    out.println(crypt);

                    if (!input.trim().isEmpty()) {
                        System.out.println("[INFO] Você está conectado. Use /msg <usuário> <mensagem>, /all <mensagem> ou /list");
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
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
