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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kakao.authorization.AuthorizationResult;
import com.kakao.authorization.Authorizer;
import com.kakao.authorization.accesstoken.AccessTokenRequest;
import com.kakao.authorization.accesstoken.GetterAccessToken;
import com.kakao.authorization.authcode.AuthorizationCodeRequest;
import com.kakao.authorization.authcode.GetterAuthorizationCode;
import com.kakao.core.R;
import com.kakao.helper.Logger;

/**
 * 내부적으로 로그인을 담당하는 Activity
 *
 * @author MJ
 */
public class LoginActivity extends Activity {
    public static final int AUTHORIZATION_CODE_REQUEST = 1;
    public static final int ACCESS_TOKEN_REQUEST = 2;
    public static final String CODE_REQUEST_KEY = "authCodeRequest";
    public static final String TOKEN_REQUEST_KEY = "tokenRequest";

    private GetterAuthorizationCode getterAuthorizationCode;
    private GetterAccessToken getterAccessToken;
    private final Authorizer.BackgroundProcessingListener backgroundProcessingListener = new BackgroundProcessListener();
    private final Authorizer.OnAuthorizationListener authorizationListener = new AuthorizationCallback();

    /**
     * authorize_code 또는 access_token 요청이 들어온다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_internal_login_activity);

        AuthorizationCodeRequest authCodeRequest;
        AccessTokenRequest accessTokenRequest;
        if (savedInstanceState != null) {
            authCodeRequest = (AuthorizationCodeRequest) savedInstanceState.getSerializable(CODE_REQUEST_KEY);
            accessTokenRequest = (AccessTokenRequest) savedInstanceState.getSerializable(TOKEN_REQUEST_KEY);
        } else {
            authCodeRequest = (AuthorizationCodeRequest) getIntent().getSerializableExtra(CODE_REQUEST_KEY);
            accessTokenRequest = (AccessTokenRequest) getIntent().getSerializableExtra(TOKEN_REQUEST_KEY);
        }
        if (authCodeRequest != null) {
            getterAuthorizationCode = new GetterAuthorizationCode(authCodeRequest);
            initializeAuthorizer(getterAuthorizationCode);
            getterAuthorizationCode.tryNextHandler();
        } else if (accessTokenRequest != null) {
            getterAccessToken = new GetterAccessToken(accessTokenRequest);
            initializeAuthorizer(getterAccessToken);
            getterAccessToken.requestAccessToken();
        } else {
            Logger.getInstance().d("Error : login activity created without request");
        }
    }

    /**
     * {@link Authorizer.BackgroundProcessingListener}에게 프로세싱이 끝났다고 알려준다.
     */
    @Override
    protected void onPause() {
        super.onPause();
        backgroundProcessingListener.onBackgroundProcessingStopped();
    }

    /**
     * authorize code 또는 access token 요청 중이라면 request를 저장해 둔다.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(getterAuthorizationCode != null)
            outState.putSerializable(CODE_REQUEST_KEY, getterAuthorizationCode.getRequest());
        if(getterAccessToken != null)
            outState.putSerializable(TOKEN_REQUEST_KEY, getterAccessToken.getRequest());
    }

    private void initializeAuthorizer(final Authorizer authorizer) {
        authorizer.setLoginActivity(this);
        authorizer.setOnAuthorizationListener(authorizationListener);
        authorizer.setBackgroundProcessingListener(backgroundProcessingListener);
    }

    private class BackgroundProcessListener implements Authorizer.BackgroundProcessingListener {
        @Override
        public void onBackgroundProcessingStarted() {
            findViewById(R.id.kakao_login_activity_progress_bar).setVisibility(View.VISIBLE);
        }

        @Override
        public void onBackgroundProcessingStopped() {
            findViewById(R.id.kakao_login_activity_progress_bar).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHORIZATION_CODE_REQUEST) {
            getterAuthorizationCode.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class AuthorizationCallback implements Authorizer.OnAuthorizationListener {
        @Override
        public void onAuthorizationCompletion(final AuthorizationResult result) {
            backgroundProcessingListener.onBackgroundProcessingStopped();

            if (result.isAuthorizationCodeRequest()) {
                getterAuthorizationCode = null;
                Session.getCurrentSession().onAuthCodeCompleted(result);
            } else if (result.isAccessTokenRequest()) {
                getterAccessToken = null;
                Session.getCurrentSession().onAccessTokenCompleted(result);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }
}
