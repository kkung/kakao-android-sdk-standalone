package com.kakao.http;

import java.net.URI;
import java.util.Map;

/**
 * @author kkung
 */
public class Response {

    private final int statusCode;
    private String statusText;
    private final Map<String, String> headers;
    private final String responseBody;
    private final URI uri;

    public Response(URI uri, int statusCode, String statusText, Map<String, String> headers, String responseBody) {
        this.uri = uri;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public URI getUri() {
        return uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public boolean hasResponseStatus() {
        return true;
    }

    public boolean hasResponseBody() {
        return responseBody != null && responseBody.length() > 0;
    }
}
