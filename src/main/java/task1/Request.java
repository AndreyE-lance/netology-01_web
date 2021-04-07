package task1;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class Request {
    private final String method;
    private final String path;
    private final List<String> headers;
    private final InputStream in;
    private final List<NameValuePair> nameValuePairList;

    private Request(String method, String path, List<String> headers, InputStream in, List<NameValuePair> nameValuePairList) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
        this.nameValuePairList = nameValuePairList;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request fromInputStream(BufferedInputStream inputStream) throws IOException {

        final var in = inputStream;
        final var limit = 4096;
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);
        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            throw new IOException();
        }
        final var method = requestLine[0];
        final var path = requestLine[1];
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        in.reset();
        in.skip(requestLineEnd);
        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        List<NameValuePair> nameValuePairList = null;

        if (!method.equals("GET")) {
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length+2);

                final var body = new String(bodyBytes);
                nameValuePairList = getPostParams(body);
            }
        } else {
            nameValuePairList = getQueryParams(path);
        }

        return new Request(method, path, headers, inputStream, nameValuePairList);
    }

    protected List<String> getPostParam(String name) throws IOException {
        List<String> result = new LinkedList<>();
        for (var i = 0; i < nameValuePairList.size(); i++) {
            if (nameValuePairList.get(i).getName().equals(name))
                result.add(nameValuePairList.get(i).getValue());
        }
        return result;
    }


    protected String getQueryParam(String name) {
        for (var i = 0; i < nameValuePairList.size(); i++) {
            if (nameValuePairList.get(i).getName().equals(name))
                return nameValuePairList.get(i).getValue();
        }
        return null;
    }

    private static List<NameValuePair> getQueryParams(String url) {
        return URLEncodedUtils.parse(url, Charset.defaultCharset(), '?');
    }

    private static List<NameValuePair> getPostParams(String params) {
        return URLEncodedUtils.parse(params, Charset.defaultCharset(), '&');
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }


}
