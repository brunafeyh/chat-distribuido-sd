package server;

import common.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int TCP_PORT = 12345;
    private static final int UDP_PORT = 12346;
    private static final Map<String, Socket> clients = new ConcurrentHashMap<>();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(TCP_PORT);
        DatagramSocket udpSocket = new DatagramSocket(UDP_PORT);

        System.out.println("[TCP] Servidor iniciado na porta " + TCP_PORT);
        System.out.println("[UDP] Servidor ouvindo na porta " + UDP_PORT);

        // Thread para escutar mensagens UDP
        new Thread(() -> listenUDP(udpSocket)).start();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            clientPool.submit(() -> handleClient(clientSocket));
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Digite seu nome de usuário:");
            System.out.println("[DEBUG] Aguardando nome do usuário...");
            String username = in.readLine();
            System.out.println("[DEBUG] Recebido: " + username);

            if (username == null || username.trim().isEmpty()) {
                out.println("Nome inválido. Conexão encerrada.");
                socket.close();
                return;
            }

            clients.put(username, socket);
            broadcast("[Servidor] " + username + " entrou no chat.", username);

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("/msg ")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length >= 3) {
                        String target = parts[1];
                        String msg = parts[2];
                        sendToUser(target, "[Privado de " + username + "]: " + msg);
                    }
                } else if (input.equals("/list")) {
                    out.println("Usuários conectados: " + clients.keySet());
                } else {
                    broadcast("[" + username + "]: " + input, username);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro com cliente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            clients.values().removeIf(s -> s.equals(socket));
        }
    }

    private static void broadcast(String message, String excludeUser) {
        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeUser)) {
                try {
                    PrintWriter out = new PrintWriter(entry.getValue().getOutputStream(), true);
                    out.println(message);
                } catch (IOException ignored) {}
            }
        }
    }

    private static void sendToUser(String user, String message) {
        Socket targetSocket = clients.get(user);
        if (targetSocket != null) {
            try {
                PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
                out.println(message);
            } catch (IOException ignored) {}
        }
    }

    private static void listenUDP(DatagramSocket socket) {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("[UDP] Recebido: " + received);
            } catch (IOException e) {
                System.out.println("Erro no UDP: " + e.getMessage());
            }
        }
    }
}
