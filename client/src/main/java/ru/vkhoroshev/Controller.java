package ru.vkhoroshev;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public ListView<String> listView;
    @FXML
    public TextField textField;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private File currentDir;
    private byte[] buffer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String host = "localhost";
        final int port = 8189;
        try {
            Socket socket = new Socket(host, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            Thread readThread = new Thread(this::readMessage);
            readThread.setDaemon(true);
            readThread.start();

            currentDir = new File(System.getProperty("user.home"));
            fillCurrentDir();

            buffer = new byte[256];

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String fileName = textField.getText();
        File currentFile = currentDir.toPath().resolve(fileName).toFile();

        try {
            outputStream.writeUTF("#SEND#FILE#");
            outputStream.writeUTF(fileName);
            outputStream.writeLong(currentFile.length());

            try (FileInputStream in = new FileInputStream(currentFile)) {
                while (true) {
                    int read = in.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                }
            }
            outputStream.flush();
            textField.clear();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void readMessage() {
        try {
            while (true) {
                String message = inputStream.readUTF();
                Platform.runLater(() -> textField.setText(message));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void fillCurrentDir() {
        listView.getItems().clear();
        listView.getItems().add("..");
        listView.getItems().addAll(currentDir.list());
    }

    @FXML
    public void toCurrentDir(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String fileName = listView.getSelectionModel().getSelectedItem();
            Path path = currentDir.toPath().resolve(fileName);

            if (Files.isDirectory(path)) {
                currentDir = path.toFile();
                fillCurrentDir();
                textField.clear();
            } else {
                textField.setText(fileName);
            }
        }
    }
}
