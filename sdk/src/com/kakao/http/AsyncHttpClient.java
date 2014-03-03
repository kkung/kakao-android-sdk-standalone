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

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author kkung
 */
public class AsyncHttpClient {


    private ExecutorService httpExecutor;
    private int defaultConnectionToInMs;
    private int defaultRequestToInMs;

    public AsyncHttpClient(ExecutorService httpExecutor, int defaultConnectionToInMs, int defaultRequestToInMs) {
        this.httpExecutor = httpExecutor;
        this.defaultConnectionToInMs = defaultConnectionToInMs;
        this.defaultRequestToInMs = defaultRequestToInMs;
    }

    public <T> void executeRequest(Request request, KakaoAsyncHandler<T> asyncHandler) {
        this.httpExecutor.submit(
                new HttpRunnable<T>(
                        request,
                        asyncHandler,
                        defaultConnectionToInMs,
                        defaultRequestToInMs
                )
        );
    }

    private static class HttpRunnable<T> implements Runnable {

        private final Request request;
        private final KakaoAsyncHandler asyncHandler;
        private final int defaultConnectionToInMs;
        private final int defaultRequestToInMs;

        public HttpRunnable(Request request, KakaoAsyncHandler<T> asyncHandler, int defaultConnectionToInMs, int defaultRequestToInMs) {
            this.request = request;
            this.asyncHandler = asyncHandler;
            this.defaultConnectionToInMs = defaultConnectionToInMs;
            this.defaultRequestToInMs = defaultRequestToInMs;
        }

        @Override
        public void run() {
            try {
                final URL url = new URL(request.getUrl());

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(defaultConnectionToInMs);
                urlConnection.setReadTimeout(defaultRequestToInMs);
                urlConnection.setRequestMethod(request.getMethod());

                for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }

                urlConnection.setDoInput(true);
                urlConnection.setInstanceFollowRedirects(true);

                InputStream in = null, err = null;
                OutputStream os = null;
                Response response;
                try {
                    byte[] responseData;
                    Object payload = request.getPayload();

                    if (request.hasPayload() && payload != null) {
                        urlConnection.setDoOutput(true);

                        if (payload instanceof HttpEntity) {
                            HttpEntity e = (HttpEntity)payload;
                            urlConnection.setRequestProperty(
                                    "Content-Type", e.getContentType().getValue()
                            );

                            long contentLength = e.getContentLength();
                            if (contentLength > 0) {
                                urlConnection.setRequestProperty(
                                        "Content-Length", String.valueOf(contentLength)
                                );
                            }

                        } else if (payload instanceof Multipart) {
                            urlConnection.setRequestProperty(
                                    "Content-Type", ((Multipart)payload).getContentType()
                            );
                        } else if (payload instanceof byte[]) {
                            urlConnection.setRequestProperty(
                                    "Content-Length", String.valueOf(((byte[]) payload).length)
                            );
                        }
                    }

                    urlConnection.connect();

                    if (request.hasPayload() && payload != null) {
                        os = urlConnection.getOutputStream();

                        if (payload instanceof HttpEntity) {
                            ((HttpEntity)payload).writeTo(os);
                        } else if (payload instanceof byte[]) {
                            os.write((byte[])payload);
                        } else if (payload instanceof Multipart) {
                            ((Multipart)payload).writeTo(os);
                        }
                    }

                    int status = urlConnection.getResponseCode();

                    if (status >= 400) {
                        err = urlConnection.getErrorStream();
                        responseData = consume(err);
                    } else {
                        in = urlConnection.getInputStream();
                        responseData = consume(in);
                    }

                    Map<String, String> headers = new HashMap<String, String>();
                    for (String key : urlConnection.getHeaderFields().keySet()) {
                        headers.put(key, urlConnection.getHeaderField(key));
                    }

                    response = new Response(
                            url.toURI(),
                            status,
                            urlConnection.getResponseMessage(),
                            headers,
                            new String(responseData, request.getCharSet())
                    );

                    if (status != HttpURLConnection.HTTP_OK) {
                        asyncHandler.handleFailureHttpStatus(
                                response,
                                url.toURI(),
                                status
                        );
                    } else {
                        asyncHandler.onCompleted(response);
                    }

                } finally {
                    if (in != null) {
                        in.close();
                        in = null;
                    }

                    if (err != null) {
                        err.close();
                        err = null;
                    }

                    if (os != null) {
                        os.close();
                        os = null;
                    }

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                asyncHandler.onThrowable(e);
            }
        }

        private byte[] consume(InputStream in) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            while(true) {
                int readlen = in.read(buf);
                if (readlen<1)
                    break;
                baos.write(buf, 0, readlen);
            }
            return baos.toByteArray();
        }
    }
}
