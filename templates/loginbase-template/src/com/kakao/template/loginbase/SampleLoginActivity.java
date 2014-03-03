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
package com.kakao.template.loginbase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.widget.LoginButton;

/**
 * 샘플에서 사용하게 될 로그인 페이지
 * 세션을 오픈한 후 action을 override해서 사용한다.
 *
 * @author MJ
 */
public class SampleLoginActivity extends Activity {
    private LoginButton loginButton;
    private final SessionCallback mySessionCallback = new MySessionStatusCallback();

    /**
     * super.onCreate를 호출하여 Session처리를 맡긴다.
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // 로그인 버튼에 로그인 결과를 받을 콜백을 설정한다.
        loginButton = (LoginButton) findViewById(R.id.com_kakao_login);
        loginButton.setLoginSessionCallback(mySessionCallback);
    }

    protected void onResume() {
        super.onResume();
        // 세션을 초기화 한다
        if(Session.initializeSession(this, mySessionCallback)){
            // 1. 세션을 갱신 중이면, 프로그레스바를 보이거나 버튼을 숨기는 등의 액션을 취한다
            loginButton.setVisibility(View.GONE);
        } else if (Session.getCurrentSession().isOpened()){
            // 2. 세션이 오픈된된 상태이면, 다음 activity로 이동한다.
            onSessionOpened();
        }
            // 3. else 로그인 창이 보인다.
    }

    private class MySessionStatusCallback implements SessionCallback {
        /**
         * 세션이 오픈되었으면 가입페이지로 이동 한다.
         */
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            SampleLoginActivity.this.onSessionOpened();
        }

        /**
         * 세션이 삭제되었으니 로그인 화면이 보여야 한다.
         * @param exception  에러가 발생하여 close가 된 경우 해당 exception
         */
        @Override
        public void onSessionClosed(final KakaoException exception) {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
            loginButton.setVisibility(View.VISIBLE);
        }

    }

    protected void onSessionOpened(){
        final Intent intent = new Intent(SampleLoginActivity.this, SampleSignupActivity.class);
        startActivity(intent);
        finish();
    }
}
