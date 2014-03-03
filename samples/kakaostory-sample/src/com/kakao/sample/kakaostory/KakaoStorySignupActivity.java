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
package com.kakao.sample.kakaostory;

import android.content.Intent;
import com.kakao.template.loginbase.SampleSignupActivity;

/**
 * 유효한 세션이 있다는 검증 후
 * me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
public class KakaoStorySignupActivity extends SampleSignupActivity {

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 사용자가 가입 상태임을 확인한 경우로 Main 페이지로 이동한다.
     */
    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, KakaoStoryMainActivity.class);
        startActivity(intent);
        finish();
    }
}
