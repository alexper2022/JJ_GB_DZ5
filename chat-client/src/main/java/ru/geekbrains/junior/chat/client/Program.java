package ru.geekbrains.junior.chat.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите своё имя: ");
        String name = scanner.nextLine();

        InetAddress address = InetAddress.getLocalHost();
        Socket socket = new Socket(address, 4300);
        Client client = new Client(socket, name);

        InetAddress inetAddress = socket.getInetAddress();

        System.out.println("InetAddress: " + inetAddress);
        String remoteIp = inetAddress.getHostAddress();
        System.out.println("Remote IP: " + remoteIp);
        System.out.println("LocalPort:" + socket.getLocalPort());
        System.out.println("Для отправки личного сообщения пользователю,");
        System.out.println("введите @name, затем пробел и сообщение");
        System.out.println("(name - имя пользователя, которому хотите отправить сообщение)");

        client.listenForMessage();
        client.sendMessage();
    }

}
