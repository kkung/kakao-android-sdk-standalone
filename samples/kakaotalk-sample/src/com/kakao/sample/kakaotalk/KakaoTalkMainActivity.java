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
package com.kakao.sample.kakaotalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.kakao.APIErrorResult;
import com.kakao.KakaoTalkHttpResponseHandler;
import com.kakao.KakaoTalkProfile;
import com.kakao.KakaoTalkService;
import com.kakao.LogoutResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.widget.ProfileLayout;

/**
 * 카카오톡 API인 프로필를 테스트 한다.
 * 유효한 세션이 있다는 검증을 {@link KakaoTalkLoginActivity}로 부터 받고 보여지는 로그인 된 페이지이다.
 */
public class KakaoTalkMainActivity extends Activity {
    private UserProfile userProfile;
    private ProfileLayout profileLayout;
    private TextView countryISOText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    protected void onResume() {
        super.onResume();
        // 로그인 하면서 caching되어 있는 profile를 그린다.
        userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            profileLayout.setUserProfile(userProfile);
        }
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(this, KakaoTalkLoginActivity.class);
        startActivity(intent);
        finish();
    }

    // profile view에서 talk profile을 update 한다.
    private void applyTalkProfileToView(final KakaoTalkProfile talkProfile) {
        if (profileLayout != null) {
            if (userProfile != null) {
                profileLayout.setUserProfile(userProfile);
            }
            final String profileImageURL = talkProfile.getProfileImageURL();
            if (profileImageURL != null)
                profileLayout.setProfileURL(profileImageURL);

            final String nickName = talkProfile.getNickName();
            if (nickName != null)
                profileLayout.setNickname(nickName);
        }
        if (countryISOText != null) {
            final String countyISO = talkProfile.getCountryISO();
            if (countyISO != null) {
                countryISOText.setText(countyISO);
            }
        }
    }

    private void onClickProfile() {
        KakaoTalkService.requestProfile(new KakaoTalkHttpResponseHandler<KakaoTalkProfile>() {
            @Override
            protected void onHttpSuccess(final KakaoTalkProfile talkProfile) {
                Toast.makeText(getApplicationContext(), "success to get talk profile", Toast.LENGTH_SHORT).show();
                applyTalkProfileToView(talkProfile);
            }

            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onNotKakaoTalkUser() {
                Toast.makeText(getApplicationContext(), "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onFailure(APIErrorResult errorResult) {
                Toast.makeText(getApplicationContext(), "failure : " + errorResult, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                redirectLoginActivity();
            }
        });
    }

    private void initializeView() {
        setContentView(R.layout.main);
        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickProfile();
            }
        });

        final Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogout();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        countryISOText = (TextView) findViewById(R.id.country);
    }
}