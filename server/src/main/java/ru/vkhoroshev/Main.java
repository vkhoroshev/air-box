package ru.vkhoroshev;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        final int port = 8189;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started.");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted.");

                FileProcessor fileProcessor = new FileProcessor(socket);
                new Thread(fileProcessor).start();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
