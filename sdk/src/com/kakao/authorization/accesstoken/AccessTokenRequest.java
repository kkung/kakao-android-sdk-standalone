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

import android.content.Context;

import com.kakao.helper.Utility;

import java.io.Serializable;

/**
 * authorization cod 또는 refresh token을 이용하여 access token을 얻어오는 요청
 * @author MJ
 */
public class AccessTokenRequest implements Serializable {
    private static final long serialVersionUID = -7167683110703808736L;

    private final String appKey;
    private final String redirectURI;
    private final String keyHash;

    private String authorizationCode;
    private String refreshToken;

    private AccessTokenRequest(final Context context, final String appKey, final String redirectURI) {
        this.appKey = appKey;
        this.redirectURI = redirectURI;
        this.keyHash = Utility.getKeyHash(context);
    }

    public static AccessTokenRequest createRequestWithAuthorizationCode(final Context context,
                                                                        final String appKey, final String redirectURI, final String authorizationCode){
        return new AccessTokenRequest(context, appKey, redirectURI).setAuthorizationCode(authorizationCode);
    }

    public static AccessTokenRequest createRequestWithRefreshToken(final Context context,
                                                                   final String appKey, final String redirectURI, final String refreshToken){
        return new AccessTokenRequest(context, appKey, redirectURI)
                .setRefreshToken(refreshToken);
    }

    public boolean isAccessTokenRequestWithAuthCode(){
        return authorizationCode != null;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    private AccessTokenRequest setAuthorizationCode(final String authorizationCode) {
        this.authorizationCode = authorizationCode;
        return this;
    }

    private AccessTokenRequest setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

}
