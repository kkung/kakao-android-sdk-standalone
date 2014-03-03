/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
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

import com.kakao.internal.Action;
import com.kakao.internal.ActionInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * 카카오링크 톡메시지 중 앱으로 연결할 url을 설정하는 Builder
 * OS별, 디바이스별 설정이 가능하다.
* @author MJ
*/
public class AppActionBuilder {
    /**
     * 앱으로 연결할 url을 디바이스별로 구분할 때 사용된다.
     */
    public enum DEVICE_TYPE {
        /**
         * 핸드폰
         */
        PHONE("phone"),
        /**
         * 패드
         */
        PAD("pad");

        private final String value;

        DEVICE_TYPE(String value) {
            this.value = value;
        }

        /**
         * 디바이스 종류의 string 값
         * 메시지를 json으로 만들때 사용된다.
         * @return 디바이스 종류의 string 값. 메시지를 json으로 만들때 사용된다.
         */
        public String getValue() {
            return value;
        }
    }

    private final Set<ActionInfo> actionInfos;

    public AppActionBuilder() {
        actionInfos = new HashSet<ActionInfo>();
    }

    /**
     * Android 기기에서 앱으로 연결시 기본 커스텀 스킴 URL(kakao[appkey]://kakaolink)에 추가할 파라미터를 설정한다.
     * 디바이스 종류 구분하지 않고 설정할 때 사용한다. 다바이스 타입을 구분하고자 할 때는 {@link #setAndroidExecuteURLParam(String, AppActionBuilder.DEVICE_TYPE)}을 이용하도록 한다.
     * @param executeURLParam 앱 연결 URL에 추가할 파라미터
     * @return 계속해서 앱 연결 설정할 수 있는 빌더를 반환한다.
     */
    public AppActionBuilder setAndroidExecuteURLParam(final String executeURLParam){
        return setAndroidExecuteURLParam(executeURLParam, null);
    }

    /**
     * Android 기기에서 앱으로 연결시 기본 커스텀 스킴 URL(kakao[appkey]://kakaolink)에 추가할 파라미터를 설정한다.
     * 디바이스 종류 구분하여 설정할 때 사용한다. 다바이스 타입을 구분할 필요가 없으시에는 {@link #setAndroidExecuteURLParam(String)}을 이용하도록 한다.
     * @param executeURLParam 앱 연결 URL에 추가할 파라미터
     * @param deviceType 디바이스 종류
     * @return 계속해서 앱 연결 설정할 수 있는 빌더를 반환한다.
     */
    public AppActionBuilder setAndroidExecuteURLParam(final String executeURLParam, final DEVICE_TYPE deviceType){
        ActionInfo androidActionInfo =  ActionInfo.createAndroidActionInfo(executeURLParam, deviceType);
        actionInfos.add(androidActionInfo);
        return this;
    }

    /**
     * iOS 기기에서 앱으로 연결시 기본 커스텀 스킴 URL(kakao[appkey]://kakaolink)에 추가할 파라미터를 설정한다.
     * 디바이스 종류 구분하지 않고 설정할 때 사용한다. 다바이스 타입을 구분하고자 할 때는 {@link #setIOSExecuteURLParam(String, AppActionBuilder.DEVICE_TYPE)}을 이용하도록 한다.
     * @param executeURLParam 앱 연결 URL에 추가할 파라미터
     * @return 계속해서 앱 연결 설정할 수 있는 빌더를 반환한다.
     */
    public AppActionBuilder setIOSExecuteURLParam(final String executeURLParam){
        ActionInfo iosActionInfo =  ActionInfo.createIOSActionInfo(executeURLParam, null);
        actionInfos.add(iosActionInfo);
        return this;
    }

    /**
     * iOS 기기에서 앱으로 연결시 기본 커스텀 스킴 URL(kakao[appkey]://kakaolink)에 추가할 파라미터를 설정한다.
     * 디바이스 종류 구분하여 설정할 때 사용한다. 다바이스 타입을 구분할 필요가 없으시에는 {@link #setIOSExecuteURLParam(String)}을 이용하도록 한다.
     * @param executeURLParam 앱 연결 URL에 추가할 파라미터
     * @param deviceType 디바이스 종류
     * @return 계속해서 앱 연결 설정할 수 있는 빌더를 반환한다.
     */
    public AppActionBuilder setIOSExecuteURLParam(final String executeURLParam, final DEVICE_TYPE deviceType){
        ActionInfo iosActionInfo =  ActionInfo.createIOSActionInfo(executeURLParam, deviceType);
        actionInfos.add(iosActionInfo);
        return this;
    }

    /**
     * 앱 연결 URL 설정이 완료되어 Action에 추가하고 이를 반환한다.
     * @return 앱 연결 URL이 설정된 Action
     * @throws KakaoLinkParseException 프로토콜에 맞지 않는 설정을 한 경우 던지는 에러
     */
    public Action build() throws KakaoLinkParseException {
        return Action.newActionApp(actionInfos.toArray(new ActionInfo[actionInfos.size()]));
    }
}
