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
package com.kakao.internal;

import android.text.TextUtils;

import com.kakao.AppActionBuilder.DEVICE_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionInfo {
    enum ACTION_INFO_OS {
        ANDROID("android"),
        IOS("ios");

        private final String value;

        ACTION_INFO_OS(String value) {
            this.value = value;
        }
    }

    private final ACTION_INFO_OS os;
    private final DEVICE_TYPE deviceType;
    private final String executeParam;

    private ActionInfo(final ACTION_INFO_OS os, final String executeParam, final DEVICE_TYPE deviceType) {
        this.os = os;
        this.executeParam = executeParam;
        this.deviceType = deviceType;
    }

    public static ActionInfo createAndroidActionInfo(final String executeParam, final DEVICE_TYPE deviceType){
        return new ActionInfo(ACTION_INFO_OS.ANDROID, executeParam, deviceType);
    }

    public static ActionInfo createIOSActionInfo(final String executeParam, final DEVICE_TYPE deviceType){
        return new ActionInfo(ACTION_INFO_OS.IOS, executeParam, deviceType);
    }

    JSONObject createJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KakaoTalkLinkProtocol.ACTIONINFO_OS, os.value);

        if (!TextUtils.isEmpty(executeParam)) {
            json.put(KakaoTalkLinkProtocol.ACTIONINFO_EXEC_PARAM, executeParam);
        }

        if (deviceType != null) {
            json.put(KakaoTalkLinkProtocol.ACTIONINFO_DEVICETYPE, deviceType.getValue());
        }
        return json;
    }
}

