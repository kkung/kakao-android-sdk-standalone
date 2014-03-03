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

import android.content.Intent;
import com.kakao.template.loginbase.SampleLoginActivity;

/**
 * 사용자 관리 API는 로그인 기반이다.
 * 세션을 오픈한 후에 가입 페이지로 넘긴다.
 * @author MJ
 */
public class UserMgmtLoginActivity extends SampleLoginActivity {

    /**
     * 세션이 오픈되었으면 가입페이지로 이동
     */
    @Override
    protected void onSessionOpened() {
        final Intent intent = new Intent(UserMgmtLoginActivity.this, UsermgmtSignupActivity.class);
        startActivity(intent);
        finish();
    }
}
