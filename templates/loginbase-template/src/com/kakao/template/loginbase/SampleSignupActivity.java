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
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;

/**
 * 유효한 세션이 있다는 검증 후
 * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
public class SampleSignupActivity extends Activity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 자동가입앱인 경우는 가입안된 유저가 나오는 것은 에러 상황.
     */
    protected void showSignup() {
        Logger.getInstance().d("not registered user");
        redirectLoginActivity();
    }

    protected void redirectMainActivity() {
//        final Intent intent = new Intent(this, SampleMainActivity.class);
//        startActivity(intent);
//        finish();
    }

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, SampleLoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            protected void onSuccess(final UserProfile userProfile) {
                Logger.getInstance().d("UserProfile : " + userProfile);
                userProfile.saveUserToCache();
                redirectMainActivity();
            }

            @Override
            protected void onNotSignedUp() {
                showSignup();
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.getInstance().d(message);
                redirectLoginActivity();
            }
        });
    }

}
