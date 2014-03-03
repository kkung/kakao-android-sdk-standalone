/**
 * Copyright 2014 Kakao Corp.
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
package com.kakao.rest;

import android.os.Message;
import com.kakao.APIErrorResult;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpRequestBuilder;
import com.kakao.http.HttpRequestTask;
import com.kakao.http.HttpResponseHandler;
import com.kakao.http.HttpTaskManager;
import com.kakao.http.KakaoAsyncHandler;
import com.kakao.http.Request;
import com.kakao.http.Response;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

/**
 * API 요청을 위한 HttpRequestTask로 세션 만료시 재갱신하여 재요청해주고,
 * 서버 에러가 발생한 경우 APIErrorResult 객체를 반환하도록 한다.
 * @param <T> : 요청이 성공한 경우 return 객체 type. HttpResponseHandler<T>, Class<T>
 * @author MJ
 */
public class APIHttpRequestTask<T> extends HttpRequestTask<T> {
    private final HttpResponseHandler<T> HttpResponseHandler;

    public APIHttpRequestTask(final Request request, final HttpResponseHandler<T> httpResponseHandler, final Class<T> returnType) {
        super(request, new APIAsyncHandler<T>(request, httpResponseHandler, returnType));
        this.HttpResponseHandler = httpResponseHandler;
    }

    public static void checkSessionAndExecute(final APIHttpRequestTask requestTask, final HttpResponseHandler responseHandler) {
        if (Session.getCurrentSession().isOpened())
            HttpTaskManager.execute(requestTask);
        else if (!requestAccessTokenWithRefreshToken(requestTask, responseHandler)) {
            failedToRefreshAccessToken(requestTask, responseHandler, "session is closed before sending the request");
        }
    }

    private static void failedToRefreshAccessToken(final APIHttpRequestTask requestTask, final HttpResponseHandler responseHandler, final String errorMsg) {
        String requestUrl = null;
        if(requestTask != null && requestTask.request != null)
            requestUrl = requestTask.request.getUrl();
        final APIErrorResult clientError = new APIErrorResult(requestUrl, errorMsg);
        responseHandler.sendMessage(Message.obtain(responseHandler, NEED_TO_LOGIN, 0, 0, clientError));
    }

    public static void addQueryParam(final HttpRequestBuilder requestBuilder, final String propertiesKey, final Map properties) throws JSONException {
        JSONObject json = new JSONObject();
        for (Object key : properties.keySet()) {
            json.put((String)key, properties.get(key));
        }

        requestBuilder.addQueryParameter(propertiesKey, json.toString());
    }

    private static class APIAsyncHandler<T> extends KakaoAsyncHandler<T> {

        public APIAsyncHandler(final Request request, final HttpResponseHandler<T> httpResponseHandler, final Class<T> returnType) {
            super(request, httpResponseHandler, returnType);
        }

        protected Void handleFailureHttpStatus(final Response response, final URI requestUri, final int httpStatusCode) throws IOException {
            switch (httpStatusCode) {
                case HttpStatus.SC_UNAUTHORIZED:
                    requestAccessTokenWithRefreshToken(new APIHttpRequestTask<T>(request, httpResponseHandler, returnType), httpResponseHandler);
                    return null;
                case HttpStatus.SC_BAD_REQUEST:
                case HttpStatus.SC_FORBIDDEN:
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    if (checkResponseBody(response)) return null;

                    JSONObject json = null;
                    try {
                        json = new JSONObject(response.getResponseBody());
                        final APIErrorResult result =  new APIErrorResult(
                                json.getInt("code"),
                                json.getString("msg")
                        );
                        result.setRequestURL(requestUri == null ? null : requestUri.toString());
                        httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, ERROR, 0, 0, result));
                    } catch (JSONException e) {
                        httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, ERROR, 0, 0, e));
                    }
                    return null;
                default:
                    sendError(response, "not expected http status");
                    return null;
            }
        }
    }

    private static boolean requestAccessTokenWithRefreshToken(final APIHttpRequestTask requestTask, final HttpResponseHandler responseHandler) {
        return Session.getCurrentSession().implicitOpen(new SessionCallback() {
            @Override
            public void onSessionOpened() {
                HttpTaskManager.execute(requestTask.updateSession());
            }

            @Override
            public void onSessionClosed(final KakaoException exception) {
                failedToRefreshAccessToken(requestTask, responseHandler, "session is closed during refreshing token for the request");
            }
        });
    }

    private HttpRequestTask updateSession() {
        request.getHeaders().put(ServerProtocol.AUTHORIZATION_HEADER_KEY, getAuthHeaderValue());
        return this;
    }

    protected void preRequest() {
        HttpResponseHandler.sendMessage(Message.obtain(HttpResponseHandler, -1, 0, 0));
    }

    protected void failedRequest(Exception e) {
        HttpResponseHandler.sendMessage(Message.obtain(HttpResponseHandler, ERROR, 0, 0, e));
    }

    public static void addCommon(final HttpRequestBuilder requestBuilder) {
        final Entry<String,String> entry = KA_HEADER.entrySet().iterator().next();
        requestBuilder.addHeader(entry.getKey(), entry.getValue());
        requestBuilder.addHeader(ServerProtocol.AUTHORIZATION_HEADER_KEY, getAuthHeaderValue());
    }

    private static String getAuthHeaderValue() {
        final StringBuilder autorizationValue = new StringBuilder();
        autorizationValue.append(ServerProtocol.AUTHORIZATION_BEARER).append(" ").append(Session.getCurrentSession().getAccessToken());
        return autorizationValue.toString();
    }
}
