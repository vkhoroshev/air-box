package ru.vkhoroshev;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public ListView<String> listView;
    @FXML
    public TextField textField;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

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

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = textField.getText();
        textField.clear();
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void readMessage() {
        try {
            while (true) {
                String message = inputStream.readUTF();
                Platform.runLater(() -> listView.getItems().add("server: " + message));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
