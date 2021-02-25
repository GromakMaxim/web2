package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public void start() throws IOException {
        SettingsReader settingsReader = new SettingsReader();

        Socket clientSocket;
        final ServerSocket serverSocket = new ServerSocket(Integer.parseInt(settingsReader.getServerPortParameterFromSettingsFile()));
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(settingsReader.getThreadsNumberFromSettingsFile()));
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                executorService.execute(new HandlerConnection(clientSocket, serverSocket));
            }
        } finally {
            /*stop(clientSocket, serverSocket);*/
            System.out.println("Сервер остановлен");
        }

    }
    /*public void stop(Socket clientSocket, ServerSocket serverSocket) throws IOException {
        clientSocket.close();
        serverSocket.close();
    }*/

}
