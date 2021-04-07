package task1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.html", "/form.html", "/post/post_test.html"); //GET POST?
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    private final Map<String, Map<String, Handler>> handlers;
    private final Map<String, Handler> hndl = new HashMap<>();

    public Server() {
        System.out.println("Сервер запущен");
        this.handlers = new HashMap<>();
    }

    public void listen(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true)
                threadPool.execute(new ConnectionThread(serverSocket.accept(), validPaths, handlers));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    protected void addHandler(String method, String path, Handler handler) {
        hndl.put(path, handler);
        handlers.put(method, hndl);
    }
}
