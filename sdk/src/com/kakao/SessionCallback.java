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

import com.kakao.exception.KakaoException;

/**
 * 세션의 상태 변경에 따른 콜백
 * 세션이 오픈되었을 때, 세션이 만료되어 닫혔을 때 세션 콜백을 넘기게 된다.
 */
public interface SessionCallback {
    /**
     * access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태.
     * 일반적으로 로그인 후의 다음 activity로 이동한다.
     */
    public void onSessionOpened();

    /**
     * memory와 cache에 session 정보가 전혀 없는 상태.
     * 일반적으로 로그인 버튼이 보이고 사용자가 클릭시 동의를 받아 access token 요청을 시도한다.
     * @param exception   close된 이유가 에러가 발생한 경우에 해당 exception.
     *
     */
    public void onSessionClosed(final KakaoException exception);
}
