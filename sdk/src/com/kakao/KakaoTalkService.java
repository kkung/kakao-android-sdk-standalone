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

import com.kakao.helper.ServerProtocol;
import com.kakao.http.HttpRequestBuilder;
import com.kakao.http.HttpRequestTask;
import com.kakao.rest.APIHttpRequestTask;

/**
 * 카카오톡 API 요청을 담당한다.
 * @author MJ
 */
public class KakaoTalkService {

    /**
     * 카카오톡 프로필 요청
     * @param responseHandler 프로필 요청 결과에 대한 handler
     */
    public static void requestProfile(final KakaoTalkHttpResponseHandler<KakaoTalkProfile> responseHandler) {
        String url = HttpRequestTask.createBaseURL(
                ServerProtocol.API_AUTHORITY,
                ServerProtocol.TALK_PROFILE_PATH
        );

        HttpRequestBuilder requestBuilder = HttpRequestBuilder.post(url);
        APIHttpRequestTask.addCommon(requestBuilder);
        APIHttpRequestTask.checkSessionAndExecute(
                new APIHttpRequestTask<KakaoTalkProfile>(
                        requestBuilder.build(),
                        responseHandler,
                        KakaoTalkProfile.class
                ),
                responseHandler
        );
    }
}
