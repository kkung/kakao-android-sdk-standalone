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
package com.kakao.authorization.authcode;

import com.kakao.LoginActivity;

import java.io.Serializable;

/**
 * authorization code와 access token을 얻는 base request
 *
 * @author MJ
 */
public class AuthorizationCodeRequest implements Serializable {
    private static final long serialVersionUID = 7078182213563726736L;
    private String appKey;
    private String redirectURI;

    public AuthorizationCodeRequest() {
    }

    public static AuthorizationCodeRequest createNewRequest(final String appKey, final String redirectURI) {
        return new AuthorizationCodeRequest(appKey, redirectURI);
    }

    private AuthorizationCodeRequest(final String appKey, final String redirectURI) {
        this.appKey = appKey;
        this.redirectURI = redirectURI;
    }

    public int getRequestCode() {
        return LoginActivity.AUTHORIZATION_CODE_REQUEST;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getRedirectURI() {
        return redirectURI;
    }
}
