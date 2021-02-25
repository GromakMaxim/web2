package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HandlerConnection implements Runnable {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public HandlerConnection(Socket clientSocket, ServerSocket serverSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            readInputHeaders();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void readInputHeaders() throws Throwable {
        SettingsReader settingsReader = new SettingsReader();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String requestLine = bufferedReader.readLine();//читаем реквест построчно
            if (requestLine != null) {//ищем непустые строки
                String[] requestParts = requestLine.split(" ");//делим строчку по разделителю
                if (requestParts.length == 3) {
                    if (requestParts[0].equalsIgnoreCase("GET") && !requestParts[1].contains("?")) {
                        String path = requestParts[1];
                        if (settingsReader.checkValidFileExtension(path)) {//если попался путь с одним из этих расширений то загружаем файл
                            final var filePath = Path.of(".", "diploma-front", path);
                            final var mimeType = Files.probeContentType(filePath);
                            final var length = Files.size(filePath);
                            outputStream.write((
                                    "HTTP/1.1 200 OK\r\n" +
                                            "Content-Type: " + mimeType + "\r\n" +
                                            "Content-Length: " + length + "\r\n" +
                                            "Connection: close\r\n" +
                                            "\r\n"
                            ).getBytes());
                            Files.copy(filePath, outputStream);
                        } else {
                            outputStream.write((
                                    "HTTP/1.1 404 Not Found\r\n" +
                                            "Content-Length: 0\r\n" +
                                            "Connection: close\r\n" +
                                            "\r\n"
                            ).getBytes());
                        }
                        outputStream.flush();
                    } else if (requestParts[0].equalsIgnoreCase("GET") && requestParts[1].contains("?")) {
                        String url = "http://localhost:" + settingsReader.getServerPortParameterFromSettingsFile() +"/" + requestParts[1];
                        printQueryParams(url);

                        outputStream.flush();
                    }
                }
            }
        }
    }

    private void printQueryParam(String name, String url) throws URISyntaxException {
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            if (param.getName().equalsIgnoreCase(name)){
                System.out.println(param.getName() + " : " + param.getValue());
            }
        }
    }

    private void printQueryParams(String url) throws URISyntaxException {
        System.out.println(URLEncodedUtils.parse(new URI(url), "UTF-8"));
    }

}
