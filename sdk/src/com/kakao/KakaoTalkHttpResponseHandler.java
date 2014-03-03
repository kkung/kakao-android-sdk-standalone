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
import com.kakao.http.HttpResponseHandler;

/**
 * 카카오톡 API 요청에 대한 응답 handler로, {@link KakaoTalkService}를 호출할 때 넘겨주고 콜백을 받는다.
 * @author MJ
 */
public abstract class KakaoTalkHttpResponseHandler<T> extends HttpResponseHandler<T> {
    /**
     *  카카오계정에 연결한 카카오톡 사용자가 아니여서 요청이 실패한 경우 호출된다.
     */
    protected abstract void onNotKakaoTalkUser();

    /**
     * 세션이 닫혀서 또는 카카오계정에 연결한 카카오톡 사용자가 아닌 경우를 제외한 이유로 요청이 실패한 경우 호출된다.
     * 아래 에러 종류에 따라 적절한 처리를 한다.
     * {@link ErrorCode#INVALID_PARAM_CODE},
     * {@link ErrorCode#INVALID_SCOPE_CODE},
     * {@link ErrorCode#NOT_SUPPORTED_API_CODE},
     * {@link ErrorCode#INTERNAL_ERROR_CODE},
     * {@link ErrorCode#NOT_EXIST_KAKAO_ACCOUNT_CODE},
     * {@link ErrorCode#CLIENT_ERROR_CODE},
     * {@link ErrorCode#EXCEED_LIMIT_CODE},
     * {@link ErrorCode#KAKAO_MAINTENANCE_CODE}
     * @param errorResult 실패한 원인이 담긴 결과
     */
    protected abstract void onFailure(final APIErrorResult errorResult);

    @Override
    protected void onHttpFailure(final APIErrorResult errorResult) {
        Logger.getInstance().d("failed : " + errorResult);
        if (errorResult.getErrorCode() == ErrorCode.NOT_EXIST_KAKAOTALK_USER_CODE) {
            onNotKakaoTalkUser();
        } else {
            onFailure(errorResult);
        }
    }
}
