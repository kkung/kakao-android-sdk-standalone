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
package com.kakao.sample.usermgmt;

import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.kakao.APIErrorResult;
import com.kakao.SignupResponseCallback;
import com.kakao.UserManagement;
import com.kakao.helper.Logger;
import com.kakao.template.loginbase.SampleSignupActivity;

/**
 * 유효한 세션이 있다는 검증 후
 * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
public class UsermgmtSignupActivity extends SampleSignupActivity {
    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, UserMgmtLoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, UsermgmtMainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void showSignup() {
        setContentView(R.layout.signup);
        final ExtraUserPropertyLayout extraUserPropertyLayout = (ExtraUserPropertyLayout) findViewById(R.id.extra_user_property);
        Button signupButton = (Button) findViewById(R.id.buttonSignup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickSignup(extraUserPropertyLayout.getProperties());
            }
        });
    }
    /**
     * 가입 입력창의 정보를 모아서 가입 API를 호출한다.
     */
    private void onClickSignup(final Map<String, String> properties) {
        UserManagement.requestSignup(new SignupResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectMainActivity();
            }
            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }
            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                String message = "failed to sign up. msg=" + errorResult;
                Logger.getInstance().d(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }, properties);
    }
}
