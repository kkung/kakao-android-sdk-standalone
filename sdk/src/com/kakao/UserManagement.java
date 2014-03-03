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
package com.kakao;

import android.os.Message;

import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpRequestBuilder;
import com.kakao.http.HttpRequestTask;
import com.kakao.http.HttpResponseHandler;
import com.kakao.rest.APIHttpRequestTask;

import org.json.JSONException;

import java.util.Map;

/**
 * UserManagement API 요청을 담당한다.
 * @author MJ
 */
public class UserManagement {

    /**
     * 사용자정보 요청
     * @param responseHandler me 요청 결과에 대한 handler
     */
    public static void requestMe(final MeResponseCallback responseHandler) {
        final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_ME_PATH);
        request(responseHandler, url);
    }

    /**
     * 로그아웃 요청
     * @param responseHandler logout 요청 결과에 대한 handler
     */
    public static void requestLogout(final LogoutResponseCallback responseHandler) {
        final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_LOGOUT_PATH);
        request(responseHandler, url);
    }

    /**
     * Unlink 요청
     * @param responseHandler unlink 요청 결과에 대한 handler
     */
    public static void requestUnlink(final UnlinkResponseCallback responseHandler) {
        final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_UNLINK_PATH);
        request(responseHandler, url);
    }

    /**
     * 가입 요청
     * @param responseHandler signup 요청 결과에 대한 handler
     * @param properties 가입시 받은 사용자 정보
     */
    public static void requestSignup(final SignupResponseCallback responseHandler, final Map properties) {
        final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_SIGNUP_PATH);
        request(responseHandler, url, properties);
    }

    /**
     * 사용자정보 저장 요청
     * @param responseHandler updateProfile 요청 결과에 대한 handler
     * @param properties 저장할 사용자 정보
     */
    public static void requestUpdateProfile(final UpdateProfileResponseCallback responseHandler, final Map properties) {
        final String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.USER_UPDATE_PROFILE_PATH);
        request(responseHandler, url, properties);
    }

    private static void request(final HttpResponseHandler<Map> responseHandler, final String url) {
        final HttpRequestBuilder requestBuilder = HttpRequestBuilder.get(url);
        APIHttpRequestTask.addCommon(requestBuilder);
        APIHttpRequestTask.checkSessionAndExecute(new APIHttpRequestTask<Map>(requestBuilder.build(), responseHandler, Map.class), responseHandler);
    }

    private static void request(final HttpResponseHandler<Map> responseHandler, final String url, final Map properties) {
        final HttpRequestBuilder requestBuilder = HttpRequestBuilder.get(url);
        APIHttpRequestTask.addCommon(requestBuilder);
        try {
            APIHttpRequestTask.addQueryParam(requestBuilder, ServerProtocol.PROPERTIES_KEY, properties);
            APIHttpRequestTask.checkSessionAndExecute(new APIHttpRequestTask<Map>(requestBuilder.build(), responseHandler, Map.class), responseHandler);
        } catch (JSONException e) {
            responseHandler.sendMessage(Message.obtain(responseHandler, HttpRequestTask.ERROR, 0, 0,
                    new APIErrorResult(requestBuilder.build().getUrl(), e.getMessage())));
        }
    }
}
