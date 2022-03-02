package ru.vkhoroshev;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Handler(Socket socket) throws IOException {
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = inputStream.readUTF();
                System.out.println("client: " + message);

                outputStream.writeUTF(message);
                outputStream.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
