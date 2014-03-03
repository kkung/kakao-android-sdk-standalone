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
 * 사용자정보 요청 ({@link UserManagement#requestMe(MeResponseCallback)}) 호출할 때 넘겨주고 콜백을 받는다.
 * @author MJ
 */
public abstract class MeResponseCallback extends UserProfileResponseCallback {

    /**
     * 로그인을 성공적으로 마친 경우로
     * 일반적으로 로그인창으로 이동하도록 구현한다.
     * @param userProfile 로그인 성공된 사용자의 프로필 정보
     */
    protected abstract void onSuccess(final UserProfile userProfile);

    /**
     * 세션 오픈은 성공했으나 사용자 정보 요청 결과 사용자 가입이 안된 상태로
     * 일반적으로 가입창으로 이동한다.
     * 자동 가입 앱이 아닌 경우에만 호출된다.
     */
    protected abstract void onNotSignedUp();

    /**
     * 사용자정보 요청 전이나 요청 중가 세션이 닫힌 경우로
     * 일반적으로 로그인 창으로 이동한다.
     * @param errorResult 세션이 닫힌 이유
     */
    protected abstract void onSessionClosedFailure(final APIErrorResult errorResult);

    /**
     * 사용자 가입이 안된 상태({@link #onNotSignedUp}) 또는 세션이 닫힌 경우({@link #onSessionClosedFailure})를 제외한 이유로 사용자정보 요청이 실패한 경우
     * 아래 에러 종류에 따라 적절한 처리를 한다.
     * {@link ErrorCode#INVALID_PARAM_CODE},
     * {@link ErrorCode#INVALID_SCOPE_CODE},
     * {@link ErrorCode#NOT_SUPPORTED_API_CODE},
     * {@link ErrorCode#INTERNAL_ERROR_CODE},
     * {@link ErrorCode#NOT_REGISTERED_PROPERTY_KEY_CODE},
     * {@link ErrorCode#CLIENT_ERROR_CODE},
     * {@link ErrorCode#EXCEED_LIMIT_CODE},
     * {@link ErrorCode#KAKAO_MAINTENANCE_CODE}
     * @param errorResult 실패한 이유
     */
    protected abstract void onFailure(final APIErrorResult errorResult);

    /**
     * {@link UserResponseCallback}을 구현한 것으로 사용자정보 요청이 성공했을 때 호출된다.
     * 사용자 정보를 캐싱하고 사용자 콜백 {@link #onSuccess(UserProfile)}를 호출한다.
     * 결과 USerProfile이 비정상이면 에러 처리한다.
     * @param userProfile User API의 성공 결과로 요청 유저의 정보
     */
    @Override
    protected void onSuccessUserProfile(final UserProfile userProfile) {
        if(userProfile == null)
            onError("MeResponseCallback : onSuccessUserProfile is called but the result userProfile is null.", new APIErrorResult(null, "the result of Me request is null."));
        else {
            userProfile.saveUserToCache();
            onSuccess(userProfile);
        }
    }

    /**
     * {@link com.kakao.http.HttpResponseHandler}를 구현한 것으로 사용자정보 요청 전 또는 요청 중에 세션이 닫혀 로그인이 필요할 때 호출된다.
     * 사용자 콜백 {@link #onSessionClosedFailure(APIErrorResult)}을 호출한다.
     * @param errorResult 세션이 닫히 계기
     */
    @Override
    protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
        Logger.getInstance().d("MeResponseCallback : session is closed before requesting user info. errorResult = " + errorResult);
        onSessionClosedFailure(errorResult);
    }

    /**
     * {@link com.kakao.http.HttpResponseHandler}를 구현한 것으로 사용자정보 요청 중에 서버에서 요청을 수행하지 못했다는 결과를 받았을 때 호출된다.
     * 가입이 안된 경우라면 {@link #onNotSignedUp()}을 호출하고, 그외의 경우 세션의 상태에 따라 {@link #onSessionClosedFailure(APIErrorResult)} 콜백 또는 {@link MeResponseCallback#onFailure(APIErrorResult)}} 콜백을 호출한다.
     * @param errorResult 실패한 결과
     */
    @Override
    protected void onHttpFailure(final APIErrorResult errorResult) {
        if (errorResult.getErrorCode() == ErrorCode.NOT_REGISTERED_USER_CODE) {
            Logger.getInstance().d("MeResponseCallback : " + errorResult);
            onNotSignedUp();
        } else {
            onError("MeResponseCallback : server error occurred during requesting user info. ", errorResult);
        }
    }

    /**
     * 에러가 발생했을 때의 처리로
     * 세션이 닫혔으면 {@link #onSessionClosedFailure(APIErrorResult)} 콜백을 아니면 {@link MeResponseCallback#onFailure(APIErrorResult)}} 콜백을 호출한다.
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
