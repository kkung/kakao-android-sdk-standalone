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
package com.kakao.authorization.accesstoken;

import com.kakao.APIErrorResult;
import com.kakao.authorization.AuthorizationResult;
import com.kakao.authorization.Authorizer;
import com.kakao.helper.Logger;
import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpRequestBuilder;
import com.kakao.http.HttpRequestTask;
import com.kakao.http.HttpResponseHandler;
import com.kakao.http.HttpTaskManager;
import com.kakao.http.KakaoAsyncHandler;
import com.kakao.http.Request;
import com.kakao.http.Response;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author MJ
 */
public class GetterAccessToken extends Authorizer {
    private static final long serialVersionUID = -93314536204104325L;

    private final AccessTokenRequest accessTokenRequest;

    public GetterAccessToken(final AccessTokenRequest accessTokenRequest) {
        this.accessTokenRequest = accessTokenRequest;
    }

    public void requestAccessToken() {
        final boolean permission = checkInternetPermission();
        if (!permission) {
            return;
        }

        final HttpRequestBuilder requestBuilder = makeAccessTokenRequest();
        final Request httpRequest = requestBuilder.build();
        HttpTaskManager.execute(new HttpRequestTask<Map>(httpRequest, new AccessTokenCallback(httpRequest, new HttpResponseHandler<Map>() {
            @Override
            protected void onHttpSuccess(final Map resultObj) {
                final AccessToken accessToken = AccessToken.createFromResponse(resultObj);
                if(accessToken == null)
                    doneOnError("AccessToken is null.");
                else
                    completed(AuthorizationResult.createSuccessAccessTokenResult(accessToken));
            }

            @Override
            protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
                // never happen
            }

            @Override
            protected void onHttpFailure(final APIErrorResult errorResult) {
                doneOnError(errorResult.toString());
            }
        })));
    }

    public AccessTokenRequest getRequest() {
        return accessTokenRequest;
    }

    private class AccessTokenCallback extends KakaoAsyncHandler<Map> {

        public AccessTokenCallback(final Request request, final HttpResponseHandler<Map> httpResponseHandler) {
            super(request, httpResponseHandler, Map.class);
        }

        @Override
        protected Void handleFailureHttpStatus(final Response response, final URI requestUri, final int httpStatusCode) throws IOException {
            if (checkResponseBody(response)) {
                return null;
            }
            sendError(response, response.getResponseBody());
            return null;
        }
    }

    protected void completed(final AuthorizationResult outcome) {
        if (onAuthorizationListener != null) {
            onAuthorizationListener.onAuthorizationCompletion(outcome);
        }
    }

    protected void doneOnError(final String resultMessage) {
        Logger.getInstance().d("GetterAccessToken: " + resultMessage);
        if (onAuthorizationListener != null) {
            final AuthorizationResult result
                    = AuthorizationResult.createAccessTokenErrorResult(resultMessage);
            onAuthorizationListener.onAuthorizationCompletion(result);
        }
    }

    private HttpRequestBuilder makeAccessTokenRequest() {
        final HttpRequestBuilder requestBuilder = HttpRequestBuilder.post(
            HttpRequestTask.createBaseURL(ServerProtocol.AUTH_AUTHORITY, ServerProtocol.ACCESS_TOKEN_PATH));
        final Entry<String,String> entry = HttpRequestTask.KA_HEADER.entrySet().iterator().next();
        requestBuilder.addHeader(entry.getKey(), entry.getValue());

        if (accessTokenRequest.isAccessTokenRequestWithAuthCode()) {
            requestBuilder.addQueryParameter(ServerProtocol.GRANT_TYPE_KEY, ServerProtocol.GRANT_TYPE_AUTHORIZATION_CODE);
            requestBuilder.addQueryParameter(ServerProtocol.CODE_KEY, accessTokenRequest.getAuthorizationCode());
            requestBuilder.addQueryParameter(ServerProtocol.REDIRECT_URI_KEY, accessTokenRequest.getRedirectURI());
        } else { //if(request.isAccessTokenRequestWithRefreshToken()) {
            requestBuilder.addQueryParameter(ServerProtocol.GRANT_TYPE_KEY, ServerProtocol.REFRESH_TOKEN_KEY);
            requestBuilder.addQueryParameter(ServerProtocol.REFRESH_TOKEN_KEY, accessTokenRequest.getRefreshToken());
        }
        requestBuilder.addQueryParameter(ServerProtocol.CLIENT_ID_KEY, accessTokenRequest.getAppKey());
        requestBuilder.addQueryParameter(ServerProtocol.ANDROID_KEY_HASH, accessTokenRequest.getKeyHash());
        return requestBuilder;
    }
}