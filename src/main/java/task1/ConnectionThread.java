package task1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;


public class ConnectionThread implements Runnable {
    protected final Socket socket;
    protected final List<String> validPaths;
    private final Map<String, Map<String, Handler>> handlers;

    public ConnectionThread(Socket socket, List<String> validPaths, Map<String, Map<String, Handler>> handlers) {
        this.socket = socket;
        this.validPaths = validPaths;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            final var requestLine = Request.fromInputStream(in);
            System.out.println();
            Map<String, Handler> hndl = handlers.get(requestLine.getMethod());
            if (hndl != null) {
                Handler h = hndl.get(requestLine.getPath());
                if (h != null) {
                    h.handle(requestLine, out);
                } else invalidPath(out);
            } else invalidPath(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invalidPath(BufferedOutputStream out) {
        try {
            out.write(("HTTP/1.1 404Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void sendRespond(String path, BufferedOutputStream out) {
        final var filePath = Path.of("src", "public", path);
        final String mimeType;
        try {
            mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            out.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
