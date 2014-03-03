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

import android.os.Message;

import com.kakao.APIErrorResult;
import com.kakao.KakaoStoryProfile;
import com.kakao.KakaoStoryUpload;
import com.kakao.KakaoTalkProfile;
import com.kakao.User;
import com.kakao.helper.JsonHelper;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * response를 받아 response에 따라 사용자가 등록한 HttpResponseHandler<T>을 불러주는 역할을 담당한다.
 * @param <T> : 요청이 성공한 경우 HttpResponseHandler<T>가 받게되는 return type
* @author MJ
*/
public abstract class KakaoAsyncHandler<T> {
    protected final HttpResponseHandler<T> httpResponseHandler;
    protected final Request request;
    protected final Class<T> returnType;

    public KakaoAsyncHandler(final Request request, final HttpResponseHandler<T> httpResponseHandler, final Class<T> returnType) {
        this.request = request;
        this.httpResponseHandler = httpResponseHandler;
        this.returnType = returnType;
    }

    public Void onCompleted(final Response response) throws Exception {
        final URI requestUri = response.getUri();
        try {
            if (!response.hasResponseStatus()) {
                sendError(response, "the response didn't have a response status");
                return null;
            }

            final int httpStatusCode = response.getStatusCode();
            if (httpStatusCode != HttpStatus.SC_OK) {
                return handleFailureHttpStatus(response, requestUri, httpStatusCode);
            } else {
                if(returnType.equals(Void.class)){
                    httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, HttpRequestTask.SUCCESS, 0, 0));
                } else {
                    Object result = null;
                    if (checkResponseBody(response)) return null;
                    if (APIErrorResult.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());
                        result = new APIErrorResult(
                                json.getInt("code"),
                                json.getString("msg")
                        );
                    } else if (User.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());
                        result = new User(json.getLong("id"));
                    } else if (KakaoTalkProfile.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());
                        result = new KakaoTalkProfile(
                                json.getString("nickName"),
                                json.getString("profileImageURL"),
                                json.getString("thumbnailURL"),
                                json.getString("countryISO")
                        );
                    } else if (KakaoStoryUpload.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());
                        result = new KakaoStoryUpload(
                                json.getString("url")
                        );
                    } else if (KakaoStoryProfile.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());

                        result = new KakaoStoryProfile(
                                json.getString("nickName"),
                                json.getString("profileImageURL"),
                                json.getString("thumbnailURL"),
                                json.getString("bgImageURL"),
                                json.getString("birthday"),
                                "+".equals(json.getString("birthdayType")) ? KakaoStoryProfile.BirthdayType.SOLAR : KakaoStoryProfile.BirthdayType.LUNAR
                        );
                    } else if (Map.class.equals(returnType)) {
                        JSONObject json = new JSONObject(response.getResponseBody());
                        result = JsonHelper.MapFromJson(json);
                    } else if (List.class.equals(returnType)) {
                        JSONArray json = new JSONArray(response.getResponseBody());
                        result = JsonHelper.ListFromJson(json);
                    } else {
                        throw new IllegalStateException("unknown result type " + returnType);
                    }
                    httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, HttpRequestTask.SUCCESS, 0, 0, result));
                }
                return null;
            }
        } catch (Exception e) {
            sendError(response, e.toString());
            return null;
        }
    }

    public void onThrowable(final Throwable t) {
        httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, HttpRequestTask.ERROR, 0, 0,
            new APIErrorResult(request.getUrl(), "error occurred during http request. t= "+ t.toString())));
    }

    protected void sendError(final Response response, final String msg) {
        httpResponseHandler.sendMessage(Message.obtain(httpResponseHandler, HttpRequestTask.ERROR, 0, 0,
            new APIErrorResult(request.getUrl(), "http status =  "+ response.getStatusText() + " msg = " + msg)));
    }

    protected boolean checkResponseBody(final Response response) {
        if (!response.hasResponseBody()) {
            sendError(response, "the response didn't have a body");
            return true;
        }
        return false;
    }

    protected abstract Void handleFailureHttpStatus(Response response, URI requestUri, int httpStatusCode) throws IOException;
}
