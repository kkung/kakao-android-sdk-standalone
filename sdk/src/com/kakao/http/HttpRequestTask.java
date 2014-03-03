/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
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
package com.kakao.http;

import android.net.Uri;
import android.os.Bundle;

import com.kakao.helper.Logger;
import com.kakao.helper.ServerProtocol;
import com.kakao.helper.SystemInfo;
import com.kakao.helper.Utility;

import java.util.HashMap;
import java.util.Set;

/**
 * @author MJ
 */
public class HttpRequestTask<T> implements Runnable {
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int NEED_TO_LOGIN = 4;

    public static final HashMap<String,String> KA_HEADER = createKAHeader();

    private static final AsyncHttpClient ASYNC_HTTP_CLIENT = newAsyncHttpClient();

    private static final int DEFAULT_CONNECTION_TO_IN_MS = 5000;
    private static final int DEFAULT_REQUEST_TO_IN_MS = 5000;

    private final KakaoAsyncHandler<T> asyncHandler;
    protected final Request request;

    public HttpRequestTask(final Request request, final KakaoAsyncHandler<T> asyncHandler){
        this.request = request;
        this.asyncHandler = asyncHandler;
    }

    private static HashMap<String, String> createKAHeader() {
        HashMap<String, String>  kaHeader = new HashMap<String, String>();
        StringBuilder kaHeaderBuilder = new StringBuilder();
        kaHeaderBuilder.append(ServerProtocol.KA_SDK_KEY).append(ServerProtocol.SDK_VERSION).append(" ")
            .append(ServerProtocol.KA_OS_KEY).append(ServerProtocol.OS_ANDROID).append("-").append(SystemInfo.OS_VERSION).append(" ")
            .append(ServerProtocol.KA_LANG_KEY).append(SystemInfo.LANGUAGE).append("-").append(SystemInfo.COUNTRY_CODE).append(" ")
            .append(ServerProtocol.KA_RES_KEY).append(SystemInfo.getDisplayResolution()).append(" ")
            .append(ServerProtocol.KA_DEVICE_KEY).append(SystemInfo.DEVICE_MODEL);

        kaHeader.put(ServerProtocol.KA_HEADER_KEY, kaHeaderBuilder.toString());
        return kaHeader;
    }

    public static String createBaseURL(final String authority, final String requestPath) {
        Uri uri = Utility.buildUri(authority, requestPath);
        return uri.toString();
    }

    @Override
    public void run() {
        try {
            preRequest();
            ASYNC_HTTP_CLIENT.executeRequest(request, asyncHandler);
        } catch (Exception e) {
            Logger.getInstance().e(e);
            failedRequest(e);
        }
    }

    protected void preRequest() {
    }

    protected void failedRequest(Exception e) {
    }

    private static AsyncHttpClient newAsyncHttpClient() {
        return new AsyncHttpClient(
                HttpTaskManager.getHttpExecutor(),
                DEFAULT_CONNECTION_TO_IN_MS,
                DEFAULT_REQUEST_TO_IN_MS
        );
    }

    public static void addQueryParams(HttpRequestBuilder requestBuilder, Bundle parameters) {
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            Object value = parameters.get(key);

            if (value == null) {
                value = "";
            }

            if (isSupportedParameterType(value)) {
                value = parameterToString(value);
            } else {
                throw new IllegalArgumentException(String.format("Unsupported parameter type for GET request: %s",
                    value.getClass().getSimpleName()));
            }

            requestBuilder.addQueryParameter(key, value.toString());
        }
    }

    private static boolean isSupportedParameterType(final Object value) {
        return value instanceof String || value instanceof Boolean || value instanceof Number;
    }

    private static String parameterToString(final Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Boolean || value instanceof Number) {
            return value.toString();
        }
        throw new IllegalArgumentException("Unsupported parameter type.");
    }


}
