package task1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConnectionThread implements Runnable {
    protected final Socket socket;
    protected final List<String> validPaths;

    public ConnectionThread(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
        //this.start();
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                socket.close();
            }

            final var path = parts[1];
            if (isValidPath(parts[1], out)) {
                sendRespond(path, out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPath(String path, BufferedOutputStream out) throws IOException {
        if (!validPaths.contains(path)) {
            out.write(("HTTP/1.1 404Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            out.flush();
            return false;
        }
        return true;
    }

    private void sendRespond(String path, BufferedOutputStream out) {
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
    }
}
