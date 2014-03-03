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
package com.kakao.template.loginfree;

import android.app.Activity;
import android.os.Bundle;

/**
 * 카카오링크로 텍스트 메시지를 전송하는 간단한 템플릿이다.
 */
public class LoginFreeTemplateActivity extends Activity {
    /**
     * Activity 생성시에 카카오링크로 텍스트 메시지를 보내도록 한다.
     * @param savedInstanceState activity 내려가지 전에 저장한 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        try {
//            KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
//            KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//            kakaoTalkLinkMessageBuilder.addText("Send my first KakaoLink");
//            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build());
//        } catch (KakaoLinkParseException e) {
//            Logger.getInstance().d(e);
//        }
    }
}
