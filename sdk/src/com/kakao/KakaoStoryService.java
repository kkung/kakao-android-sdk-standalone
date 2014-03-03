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

import android.os.Bundle;

import com.kakao.helper.ServerProtocol;
import com.kakao.http.FilePart;
import com.kakao.http.HttpRequestBuilder;
import com.kakao.http.HttpRequestTask;
import com.kakao.rest.APIHttpRequestTask;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 카카오스토리 API 요청을 담당한다.
 * @author MJ
 */
public class KakaoStoryService {

    /**
     * 카카오스토리 프로필 요청
     * @param responseHandler 프로필 요청 결과에 대한 handler
     */
    public static void requestProfile(final KakaoStoryHttpResponseHandler<KakaoStoryProfile> responseHandler) {
        String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_PROFILE_PATH);
        HttpRequestBuilder requestBuilder = HttpRequestBuilder.post(url);
        APIHttpRequestTask.addCommon(requestBuilder);
        APIHttpRequestTask.checkSessionAndExecute(
                new APIHttpRequestTask<KakaoStoryProfile>(
                        requestBuilder.build(),
                        responseHandler,
                        KakaoStoryProfile.class
                ),
                responseHandler
        );
    }

    /**
     * 카카오스토리에 포스팅 요청전에 이미지 업로드 요청
     * @param responseHandler 이미지 업로드 요청 결과에 대한 handler
     * @param file 업로드할 이미지 파일
     * @throws FileNotFoundException 업로드할 파일이 존재하는 않는 경우 발생한다.
     */
    public static void requestUpload(final KakaoStoryHttpResponseHandler<KakaoStoryUpload> responseHandler, final File file) throws FileNotFoundException {
        String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_UPLOAD_PATH);
        HttpRequestBuilder requestBuilder = HttpRequestBuilder.post(url);
        APIHttpRequestTask.addCommon(requestBuilder);

        requestBuilder.setBodyEncoding(ServerProtocol.BODY_ENCODING);
        requestBuilder.addBodyPart(new FilePart(ServerProtocol.FILE_KEY, file));

        APIHttpRequestTask.checkSessionAndExecute(new APIHttpRequestTask<KakaoStoryUpload>(requestBuilder.build(), responseHandler, KakaoStoryUpload.class), responseHandler);
    }

    /**
     * 카카오스토리에 포스팅 요청
     * @param responseHandler 포스팅 요청 결과에 대한 handler
     * @param parameters 포스트때 사용할 정보
     */
    public static void requestPost(final KakaoStoryHttpResponseHandler<Void> responseHandler,
                                   final Bundle parameters) {
        String url = HttpRequestTask.createBaseURL(ServerProtocol.API_AUTHORITY, ServerProtocol.STORY_POST_PATH);
        HttpRequestBuilder requestBuilder = HttpRequestBuilder.post(url);
        APIHttpRequestTask.addCommon(requestBuilder);

        APIHttpRequestTask.addQueryParams(requestBuilder, parameters);
        APIHttpRequestTask.checkSessionAndExecute(new APIHttpRequestTask<Void>(requestBuilder.build(), responseHandler, Void.class), responseHandler);
    }

}
