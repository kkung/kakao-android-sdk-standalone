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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.kakao.core.R.string;
import com.kakao.helper.TalkProtocol;
import com.kakao.helper.Utility;

/**
 * 카카오링크 서비스를 사용하기 위한 class로 앱당 하나 존재한다.
 * 하나의 앱에서 여러 메시지를 만들때는 메시지마다 KakaoTalkLinkMessageBuilder를 생성해야한다.
 * 현재는 카카오톡으로 메시지 전송 API가 제공된다.
 */
public class KakaoLink {
    private static final String APP_KEY_PROPERTY = "com.kakao.sdk.AppKey";
    private static String appKey;
    private static String appVer = "";

    protected Context context;

    /**
     * 카카오링크 API를 제공하는 singleton KakaoLink를 얻는다.
     */
    public static KakaoLink getKakaoLink(Context context) throws KakaoLinkParseException{
        if (appKey == null)
            appKey = Utility.getMetadata(context, APP_KEY_PROPERTY);
        if (TextUtils.isEmpty(appKey))
            throw new KakaoLinkParseException(context.getString(string.com_kakao_alert_appKey));
        else {
            final PackageInfo packageInfo = Utility.getPackageInfo(context);
            if (packageInfo != null)
                appVer = String.valueOf(packageInfo.versionCode);
        }

        return new KakaoLink(context);
    }

    protected KakaoLink(Context context) {
        this.context = context;
    }

    /**
     * 카카오톡으로 전송할 메시지를 구성하는 Builder를 생성한다.
     * @return 생성된 KakaoTalkLinkMessageBuilder
     */
    public KakaoTalkLinkMessageBuilder createKakaoTalkLinkMessageBuilder() {
        return new KakaoTalkLinkMessageBuilder(appKey, appVer);
    }


    /**
     * 메시지 구성을 끝낸 후 카카오톡으로 메시지를 보낸다.
     * @param linkMessage 보낼 메시지를 구성하고 있는 contents
     * @param callerActivity 카카오톡을 실행시킬 activity
     */
    public void sendMessage(final String linkMessage, final Activity callerActivity) throws KakaoLinkParseException {
        final Intent intent = TalkProtocol.createKakakoTalkLinkIntent(context, linkMessage);
        if (intent == null) {
            throw new KakaoLinkParseException(context.getString(string.com_kakao_alert_install_kakaotalk));
        } else {
            callerActivity.startActivity(intent);
        }
    }
}
