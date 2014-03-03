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
package com.kakao.authorization.authcode;

import android.app.Activity;
import android.content.Intent;

import com.kakao.authorization.AuthorizationResult;
import com.kakao.authorization.Authorizer;
import com.kakao.helper.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * authorization code를 얻는 세가지 방법을 중계하는 역할
 * 우선순위에 따라 try하며 실패하면 다음 방법으로 넘어감.
 *
 * @author MJ
 */
public class GetterAuthorizationCode extends Authorizer {
    private static final long serialVersionUID = -5915324103491253588L;

    private final AuthorizationCodeRequest authorizationCodeRequest;
    private final List<AuthorizationCodeHandler> authenticationCodeHandlers = new ArrayList<AuthorizationCodeHandler>();

    private AuthorizationCodeHandler currentHandler;

    public GetterAuthorizationCode(final AuthorizationCodeRequest authCodeRequest) {
        authorizationCodeRequest = authCodeRequest;
        authenticationCodeHandlers.add(new LoggedInTalkAuthHandler(this));  // talk에 login되어 있는 계정이 있는 경우
        authenticationCodeHandlers.add(new LoggedOutTalkAuthHandler(this)); // talk에 login되어 있는 계정이 없는 경우
        authenticationCodeHandlers.add(new WebViewAuthHandler(this)); // talk이 install되어 있지 않는 경우
    }

    public void tryNextHandler() {
        final Iterator<AuthorizationCodeHandler> iterator = authenticationCodeHandlers.iterator();
        while (iterator.hasNext()) {
            currentHandler = iterator.next();
            iterator.remove();
            if (tryCurrentHandler()) {
                return;
            }
        }
        // handler를 끝까지 돌았는데도 authorization code를 얻지 못했으면 error
        doneOnError("Failed to get Authorization Code.");
    }

    public AuthorizationCodeRequest getRequest() {
        return authorizationCodeRequest;
    }

    private boolean tryCurrentHandler() {
        if (currentHandler.needsInternetPermission() && !checkInternetPermission()) {
            return false;
        } else {
            return currentHandler.tryAuthorize(authorizationCodeRequest);
        }
    }

    public void cancelCurrentHandler() {
        if (currentHandler != null) {
            currentHandler.cancel();
        }
    }

    public boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == authorizationCodeRequest.getRequestCode()) {
            return currentHandler.onActivityResult(requestCode, resultCode, data);
        } else {
            return false;
        }
    }

    Activity getCallerActivity() {
        return loginActivity;
    }

    protected void completed(final AuthorizationResult result) {
        clear();
        if (onAuthorizationListener != null) {
            onAuthorizationListener.onAuthorizationCompletion(result);
        }
    }

    protected void doneOnError(final String resultMessage) {
        Logger.getInstance().d("GetterAuthorizationCode : " + resultMessage);
        clear();

        if (onAuthorizationListener != null) {
            AuthorizationResult result
                    = AuthorizationResult.createAuthCodeErrorResult(resultMessage);
            onAuthorizationListener.onAuthorizationCompletion(result);
        }
    }

    void clear() {
        currentHandler = null;
    }
}
