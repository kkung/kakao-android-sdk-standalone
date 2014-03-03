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

import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpResponseHandler;

import java.util.Map;

/**
 * {@link UserManagement} API 호출시 넘겨주는 콜백으로 각 API 콜백의 상위 콜백이다.
 * 이 클래스를 상속 받아 아래 세 method를 구현한 callback을 각 API 요청시 param으로 넘겨주면 된다.
 * <p/>
 * {@link UserResponseCallback#onSuccessUser(User user)}
 * {@link com.kakao.http.HttpResponseHandler#onHttpSessionClosedFailure(APIErrorResult)},
 * {@link com.kakao.http.HttpResponseHandler#onHttpFailure(APIErrorResult)},
 *
 * @author MJ
 */

public abstract class UserResponseCallback extends HttpResponseHandler<Map> {

    /**
     * 요청이 성공한 경우로 결과객체를 User객체로 만들어서 {@link #onSuccessUser(User)}을 호출한다.
     * @param userMap 요청결과
     */
    @Override
    protected void onHttpSuccess(final Map userMap) {
        final Number userIdNumber = (Number) userMap.get(ServerProtocol.USER_ID_KEY);
        final long userId = userIdNumber.longValue();

        final User user = new User(userId);
        onSuccessUser(user);
    }

    /**
     * User API 요청이 성공한 경우, 성공 결과를 {@link User} type으로 받아서 결과 처리를 한다.
     * @param user User API의 성공 결과로 요청 유저의 정보
     */
    protected abstract void onSuccessUser(final User user);
}
