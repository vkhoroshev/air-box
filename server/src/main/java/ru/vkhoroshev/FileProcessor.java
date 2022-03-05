package ru.vkhoroshev;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class FileProcessor implements Runnable {
    private static final int SIZE = 256;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private File currentDir;
    private byte[] buffer;

    public FileProcessor(Socket socket) throws IOException {
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.currentDir = new File(System.getProperty("user.home") + "\\serverDir");
        this.buffer = new byte[SIZE];
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = inputStream.readUTF();
                if (message.equals("#SEND#FILE#")) {
                    String fileName = inputStream.readUTF();
                    long size = inputStream.readLong();

                    System.out.println("Created file: " + fileName);
                    System.out.println("File size: " + size);

                    Path path = currentDir.toPath().resolve(fileName);
                    try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                        for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {
                            int read = inputStream.read(buffer);
                            out.write(buffer, 0, read);
                        }
                    }
                    outputStream.writeUTF("File uploaded");
                    outputStream.flush();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
