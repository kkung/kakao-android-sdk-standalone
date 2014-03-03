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
package com.kakao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.core.R;

/**
 * 로그인 버튼
 * </br>
 * 로그인 layout에 {@link LoginButton}을 선언하여 사용한다.
 * @author MJ
 */
public class LoginButton extends FrameLayout {
    private SessionCallback loginSessionCallback;

    public LoginButton(Context context) {
        super(context);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 로그인 결과를 받을 세션 콜백을 설정한다.
     * @param sessionCallback 로그인 결과를 받을 세션 콜백
     */
    public void setLoginSessionCallback(final SessionCallback sessionCallback){
        this.loginSessionCallback = sessionCallback;
    }

    /**
     * 로그인 버튼 클릭시 세션을 오픈하도록 설정한다.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflate(getContext(), R.layout.kakao_login_layout, this);
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Session.getCurrentSession().open(loginSessionCallback);
            }
        });
    }
}
