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

import android.net.Uri;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kkung
 */
public abstract class HttpRequestBuilder {

    public static HttpRequestBuilder get(final String url) {
        return new HttpGetRequestBuilder(url);
    }

    public static HttpRequestBuilder post(final String url) {
        return new HttpPostRequestBuilder(url);
    }


    protected final String url;
    protected List<NameValuePair> parameters;
    protected Map<String, String> headers;
    protected String charset;

    protected HttpRequestBuilder(final String url) {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url == null or empty");
        }
        this.url = url;
        this.parameters = new ArrayList<NameValuePair>();
        this.headers = new HashMap<String, String>();
        this.charset = Charset.forName("UTF-8").name();
    }

    public HttpRequestBuilder addQueryParameter(final String key, final String value) {
        this.parameters.add(new BasicNameValuePair(key, value));
        return this;
    }

    public HttpRequestBuilder addHeader(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    public abstract Request build();

    public HttpRequestBuilder setBodyEncoding(String bodyEncoding) {
        this.charset = bodyEncoding;
        return this;
    }

    public abstract void addBodyPart(BodyPart part);


    private static class HttpGetRequestBuilder extends HttpRequestBuilder {

        protected HttpGetRequestBuilder(String url) {
            super(url);
        }

        @Override
        public Request build() {
            final Uri.Builder builder = new Uri.Builder();
            final Uri uri = Uri.parse(url);

            builder.scheme(uri.getScheme()).path(uri.getPath()).authority(uri.getAuthority());
            for (NameValuePair pair : parameters) {
                builder.appendQueryParameter(pair.getName(), pair.getValue());
            }

            final String buildUrl = builder.build().toString();

            return new Request() {
                @Override
                public String getUrl() {
                    return buildUrl;
                }

                @Override
                public Map<String, String> getHeaders() {
                    return headers;
                }

                @Override
                public String getMethod() {
                    return "GET";
                }

                @Override
                public boolean hasPayload() {
                    return false;
                }

                @Override
                public String getCharSet() {
                    return charset;
                }

                @Override
                public Object getPayload() {
                    return null;
                }
            };
        }

        @Override
        public void addBodyPart(BodyPart part) {
            throw new UnsupportedOperationException();
        }
    }

    private static class HttpPostRequestBuilder extends HttpRequestBuilder {

        protected Multipart multipart;

        protected HttpPostRequestBuilder(String url) {
            super(url);
            multipart = null;
        }

        @Override
        public Request build() {
            final Object[] payload = new Object[]{null};

            if (multipart != null) {
                payload[0] = multipart;
            } else {
                if (parameters != null && parameters.size() > 0) {
                    UrlEncodedFormEntity entity = null;
                    try {
                        entity = new UrlEncodedFormEntity(parameters, charset);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    payload[0] = entity;
                }
            }

            return new Request() {
                @Override
                public String getUrl() {
                    return url;
                }

                @Override
                public Map<String, String> getHeaders() {
                    return headers;
                }

                @Override
                public String getMethod() {
                    return "POST";
                }

                @Override
                public boolean hasPayload() {
                    return payload[0] != null;
                }

                @Override
                public String getCharSet() {
                    return charset;
                }

                @Override
                public Object getPayload() {
                    return payload[0];
                }
            };
        }

        @Override
        public void addBodyPart(BodyPart part) {
            if (multipart == null) {
                multipart = new Multipart(charset);
            }

            multipart.addBodyPart(part);
        }
    }
}
