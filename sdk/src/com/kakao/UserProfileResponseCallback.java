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

import com.kakao.http.HttpResponseHandler;

import java.util.Map;

/**
 * {@link UserManagement} API중 UserProfile을 결과로 받는 요청에 대한 응답 handler로 공통된 부분과 default 동작이 구현되어 있다.
 * 이 클래스를 상속 받아 아래 세 method를 구현한 callback을 각 API 요청시 param으로 넘겨주면 된다.
 * <p/>
 * {@link UserProfileResponseCallback#onSuccessUserProfile(UserProfile user)}
 * {@link com.kakao.http.HttpResponseHandler#onHttpSessionClosedFailure(APIErrorResult)},
 * {@link com.kakao.http.HttpResponseHandler#onHttpFailure(APIErrorResult)},
 *
 * @author MJ
 */

public abstract class UserProfileResponseCallback extends HttpResponseHandler<Map> {

    /**
     * 요청이 성공한 경우로 결과객체를 UserProfile객체로 만들어서 {@link #onSuccessUserProfile(UserProfile)}을 호출한다.
     * @param userMap 요청결과
     */
    @Override
    protected void onHttpSuccess(final Map userMap) {
        final UserProfile userProfile = UserProfile.createFromResponse(userMap);

        onSuccessUserProfile(userProfile);
    }

    /**
     * User API 요청이 성공한 경우, 성공 결과를 {@link UserProfile} type으로 받아서 결과 처리를 한다.
     * @param userProfile User API의 성공 결과로 요청 유저의 정보
     */
    protected abstract void onSuccessUserProfile(final UserProfile userProfile);

}
