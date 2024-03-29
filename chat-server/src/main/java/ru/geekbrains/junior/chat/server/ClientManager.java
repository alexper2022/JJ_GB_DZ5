package ru.geekbrains.junior.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {

    public static ArrayList<ClientManager> clients = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public ClientManager(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clients.add(this);
            //TODO: ...
            name = bufferedReader.readLine();
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Удаление клиента из коллекции
        removeClient();
        try {
            // Завершаем работу буфера на чтение данных
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            // Завершаем работу буфера для записи данных
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            // Закрытие соединения с клиентским сокетом
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаление клиента из коллекции
     */
    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Server: " + name + " покинул чат.");
    }

    /**
     * Отправка сообщения всем слушателям
     *
     * @param message сообщение
     */
    private void broadcastMessage(String message) {
        for (ClientManager client : clients) {
            try {
                if (!client.equals(this) && message != null) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void broadcastMessage(String adresat, String message) {
        for (ClientManager client : clients) {
            try {
                if (client.name.equals(adresat) && message != null) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                    break;
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        String[] privateMessage;
        while (!socket.isClosed()) {
            // Чтение данных
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.startsWith(name + ": @")) {
                    //отправка приватного сообщения
                    privateMessage = parseMessageFromClient(messageFromClient);
                    broadcastMessage(privateMessage[0], privateMessage[1]);
                } else {
                    // Отправка данных всем слушателям
                    broadcastMessage(messageFromClient);
                }


            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private String[] parseMessageFromClient(String messageFromClient) {
        String[] result = new String[2];
        String nameClientForMessage = "";
        char[] message = messageFromClient.replaceFirst(name + ": @", "").toCharArray();
        for (Character ch : message) {
            if (!Character.isWhitespace(ch)) {
                nameClientForMessage += ch;
            } else {
                break;
            }
        }
        result[0] = nameClientForMessage;
        result[1] = messageFromClient.replaceFirst(": @" + result[0], "@");
        return result;
    }
}
