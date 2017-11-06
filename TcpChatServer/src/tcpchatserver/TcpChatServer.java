/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchatserver;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victor
 */
public class TcpChatServer {

    Map<String, LocalDateTime> usuarios = new HashMap<>();
    List<String> mensajes = new ArrayList<>();

    public void doLogin(BufferedReader desdeCliente, PrintWriter haciaCliente) throws IOException {
        String nick = desdeCliente.readLine();
        if (!usuarios.containsKey(nick)) {
            haciaCliente.println("ok");
            haciaCliente.println(Math.max(mensajes.size() - 3, -1));
            usuarios.put(nick, LocalDateTime.now());
        } else {
            haciaCliente.println("nook");
        }
    }

    private void doPoll(BufferedReader desdeCliente, PrintWriter haciaCliente) throws IOException {
        int n = Integer.parseInt(desdeCliente.readLine());
        int num = mensajes.size() - n - 1;
        haciaCliente.println(num);
        for (int i = n + 1; i < mensajes.size(); i++) {
            haciaCliente.println(mensajes.get(i));
        }
    }

    private void doChat(BufferedReader desdeCliente, PrintWriter haciaCliente) throws IOException {
        String nick = desdeCliente.readLine();
        String msg = desdeCliente.readLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL-dd HH:mm:ss");
        mensajes.add(String.format("[%s] %s > %s", LocalDateTime.now().format(formatter),nick, msg));
    }

    public void run() {

        mensajes.add("*** chat started ***");

        ServerSocket socketServidor = null;
        try {
            socketServidor = new ServerSocket(6789);
        } catch (IOException ex) {
            Logger.getLogger(TcpChatServer.class.getName()).log(Level.SEVERE, "No es posible arrancar servidor. ¿Puerto ocupado?", ex);
        }

        while (true) {
            try (Socket conexion = socketServidor.accept()) {
                BufferedReader desdeCliente = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                PrintWriter haciaCliente = new PrintWriter(conexion.getOutputStream());
                haciaCliente.println("Holaaaa");
                String word = desdeCliente.readLine();
                switch (word.toUpperCase()) {
                    case "LOGIN":
                        doLogin(desdeCliente, haciaCliente);
                        break;
                    case "POLL":
                        doPoll(desdeCliente, haciaCliente);
                        break;
                    case "CHAT":
                        doChat(desdeCliente, haciaCliente);
                        break;
                }
                haciaCliente.flush();
            } catch (IOException ex) {
                Logger.getLogger(TcpChatServer.class.getName()).log(Level.SEVERE, "No se pudor realizar una operación de I/O", ex);
            }
        }

    }

    public static void main(String[] args) {
        new TcpChatServer().run();
    }

}
