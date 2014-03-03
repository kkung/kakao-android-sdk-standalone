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

import com.kakao.helper.Logger;

/**
 * 가입 요청 ({@link UserManagement#requestSignup(SignupResponseCallback, java.util.Map)}) 호출할 때 넘겨주고 콜백을 받는다.
 * @author MJ
 */
public abstract class SignupResponseCallback extends UserResponseCallback {
    /**
     * 가입을 성공적으로 마친 경우로
     * 일반적으로 로그인창으로 이동하도록 구현한다.
     * @param userId 가입자의 id
     */
    protected abstract void onSuccess(final long userId);

    /**
     * 가입 요청 전이나 요청 중가 세션이 close된 경우로 일반적으로 로그인창으로 이동하도록 구현한다.
     * @param errorResult 세션이 닫힌 이유
     */
    protected abstract void onSessionClosedFailure(final APIErrorResult errorResult);

    /**
     * 세션이 닫힌 경우({@link #onSessionClosedFailure})를 제외한 이유로 가입 요청이 실패한 경우
     * 아래 에러 종류에 따라 적절한 처리를 한다.
     * {@link ErrorCode#INVALID_PARAM_CODE},
     * {@link ErrorCode#INVALID_SCOPE_CODE},
     * {@link ErrorCode#NOT_SUPPORTED_API_CODE},
     * {@link ErrorCode#INTERNAL_ERROR_CODE},
     * {@link ErrorCode#ALREADY_REGISTERED_USER_CODE},
     * {@link ErrorCode#NOT_REGISTERED_PROPERTY_KEY_CODE},
     * {@link ErrorCode#CLIENT_ERROR_CODE},
     * {@link ErrorCode#EXCEED_LIMIT_CODE},
     * {@link ErrorCode#KAKAO_MAINTENANCE_CODE}
     * @param errorResult 실패한 이유
     */
    protected abstract void onFailure(final APIErrorResult errorResult);

    /**
     * {@link UserResponseCallback}을 구현한 것으로 가입 요청이 성공했을 때 호출된다.
     * 사용자 콜백 {@link #onSuccess(long)}을 호출한다.
     * 결과 User 객체가 비정상이면 에러처리한다.
     * @param user 가입된 사용자 id를 포함한 사용자 객체
     */
    @Override
    protected void onSuccessUser(final User user) {
        if(user == null || user.getId() <= 0)
            onError("SignupResponseCallback : onSuccessUser is called but the result user is null.", new APIErrorResult(null, "the result of signup request is null."));
        else
            onSuccess(user.getId());
    }

    /**
     * {@link com.kakao.http.HttpResponseHandler}를 구현한 것으로 가입 요청 전 또는 요청 중에 세션이 닫혀 로그인이 필요할 때 호출된다.
     * 사용자 콜백 {@link #onSessionClosedFailure(APIErrorResult)}을 호출한다.
     * @param errorResult 세션이 닫히 계기
     */
    @Override
    protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
        Logger.getInstance().d("SignupResponseCallback : session is closed before requesting signup. errorResult = " + errorResult);
        onSessionClosedFailure(errorResult);
    }

    /**
     * {@link com.kakao.http.HttpResponseHandler}를 구현한 것으로 가입 요청 중에 서버에서 요청을 수행하지 못했다는 결과를 받았을 때 호출된다.
     * 세션의 상태에 따라 {@link #onSessionClosedFailure(APIErrorResult)} 콜백 또는 {@link #onFailure(APIErrorResult)}} 콜백을 호출한다.
     * @param errorResult 실패한 결과
     */
    @Override
    protected void onHttpFailure(final APIErrorResult errorResult) {
        onError("SignupResponseCallback : server error occurred during requesting signup.", errorResult);
    }

    /**
     * 에러가 발생했을 때의 처리로
     * 세션이 닫혔으면 {@link #onSessionClosedFailure(APIErrorResult)} 콜백을 아니면 {@link #onFailure(APIErrorResult)}} 콜백을 호출한다.
     * @param msg 로깅 메시지
     * @param errorResult 에러 발생원인
     */
    private void onError(final String msg, final APIErrorResult errorResult) {
        Logger.getInstance().d(msg + errorResult);
        if (!Session.getCurrentSession().isOpened())
            onSessionClosedFailure(errorResult);
        else
            onFailure(errorResult);
    }
}
