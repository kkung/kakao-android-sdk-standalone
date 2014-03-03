/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
