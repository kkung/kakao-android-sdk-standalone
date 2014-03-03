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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.MeResponseCallback;
import com.kakao.UnlinkResponseCallback;
import com.kakao.UpdateProfileResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;
import com.kakao.sample.usermgmt.R.id;
import com.kakao.widget.ProfileLayout;

/**
 * 가입된 사용자가 보게되는 메인 페이지로 사용자 정보 불러오기/update, 로그아웃, 탈퇴 기능을 테스트 한다.
 */
public class UsermgmtMainActivity extends Activity {
    private UserProfile userProfile;
    private ProfileLayout profileLayout;
    private ExtraUserPropertyLayout extraUserPropertyLayout;

    /**
     * 로그인 또는 가입창에서 넘긴 유저 정보가 있다면 저장한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        userProfile = UserProfile.loadFromCache();
        if(userProfile != null)
            showProfile();
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(this, UserMgmtLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectSignupActivity() {
        Intent intent = new Intent(this, UsermgmtSignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 사용자의 정보를 변경 저장하는 API를 호출한다.
     */
    private void onClickUpdateProfile() {
        final Map<String, String> properties = extraUserPropertyLayout.getProperties();

        UserManagement.requestUpdateProfile(new UpdateProfileResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                UserProfile.updateUserProfile(userProfile, properties);
                if (userProfile != null)
                    userProfile.saveUserToCache();
                Toast.makeText(getApplicationContext(), "success to update user profile", Toast.LENGTH_SHORT).show();
                Logger.getInstance().d("success to update user profile" + userProfile);
                showProfile();
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                String message = "failed to update user profile. msg=" + errorResult;
                Logger.getInstance().d(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }, properties);
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                Logger.getInstance().d("failed to sign up. msg=" + apiErrorResult);
                redirectLoginActivity();
            }
        });
    }

    private void onClickUnlink() {
        final String appendMessage = getString(com.kakao.core.R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
            .setMessage(appendMessage)
            .setPositiveButton(getString(com.kakao.core.R.string.com_kakao_ok_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserManagement.requestUnlink(new UnlinkResponseCallback() {
                            @Override
                            protected void onSuccess(final long userId) {
                                redirectLoginActivity();
                            }

                            @Override
                            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                                redirectLoginActivity();
                            }

                            @Override
                            protected void onFailure(final APIErrorResult errorResult) {
                                Logger.getInstance().d("failure to unlink. msg = " + errorResult);
                                redirectLoginActivity();
                            }
                        });
                        dialog.dismiss();
                    }
                })
            .setNegativeButton(getString(com.kakao.core.R.string.com_kakao_cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }


    private void showProfile() {
        if(profileLayout != null)
            profileLayout.setUserProfile(userProfile);
        if(extraUserPropertyLayout != null)
            extraUserPropertyLayout.showProperties(userProfile.getProperties());
    }

    private void initializeView() {
        setContentView(R.layout.main);
        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button buttonMe = (Button) findViewById(R.id.buttonMe);
        buttonMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                profileLayout.requestMe();
            }
        });

        final Button buttonUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });

        final Button logoutButton = (Button) findViewById(id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickLogout();
            }
        });

        final Button unlinkButton = (Button) findViewById(id.unlink_button);
        unlinkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickUnlink();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        profileLayout.setMeResponseCallback(new MeResponseCallback() {
            @Override
            protected void onSuccess(final UserProfile userProfile) {
                Toast.makeText(getApplicationContext(), "success to get user profile", Toast.LENGTH_SHORT).show();
                if (userProfile != null) {
                    UsermgmtMainActivity.this.userProfile = userProfile;
                    userProfile.saveUserToCache();
                    showProfile();
                }
            }

            @Override
            protected void onNotSignedUp() {
                redirectSignupActivity();
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.getInstance().d(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        extraUserPropertyLayout = (ExtraUserPropertyLayout) findViewById(R.id.extra_user_property);
    }
}
