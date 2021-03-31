package task1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.html");
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private final static int PORT = 53535;
    private ServerSocket serverSocket;

    public Server() {
        System.out.println("Сервер запущен");
        waitingConnection();
    }

    private void waitingConnection() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (true)
                threadPool.execute(new ConnectionThread(serverSocket.accept(),validPaths));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
