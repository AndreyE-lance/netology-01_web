package task1;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;
    private final List<NameValuePair> nameValuePairList;

    private Request(String method, String path, Map<String, String> headers, InputStream in, List<NameValuePair> nameValuePairList) {
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request fromInputStream(InputStream inputStream) throws IOException {
        final var in = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException();
        }
        var method = parts[0];
        List<NameValuePair> nameValuePairList = getQueryParams(parts[1]);
        var path = nameValuePairList.get(0).getName();
        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            var indexOf = line.indexOf(":");
            var headerName = line.substring(0, indexOf);
            var headerValue = line.substring(indexOf + 2);
            headers.put(headerName, headerValue);
        }
        return new Request(method, path, headers, inputStream, nameValuePairList);
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

}
