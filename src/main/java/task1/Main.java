package task1;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.addHandler("GET", "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                sendRespond(request, responseStream);
            }
        });

        server.addHandler("GET", "/spring.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                sendRespond(request, responseStream);
            }
        });

        server.addHandler("GET", "/main/main.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                sendRespond(request, responseStream);
            }
        });

        server.addHandler("GET", "/form.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                sendRespond(request, responseStream);
            }
        });

        server.addHandler("POST", "/post/post_test.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                sendRespond(request, responseStream);
            }
        });

        server.listen(53535);
    }

    public static void sendRespond(Request request, BufferedOutputStream responseStream) {
        final var filePath = Path.of("src", "public", request.getPath());
        final String mimeType;
        try {
            mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            Files.copy(filePath, responseStream);
            responseStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
