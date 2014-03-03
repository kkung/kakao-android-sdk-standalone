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
package com.kakao.http;

import android.os.Handler;
import android.os.Message;

import com.kakao.APIErrorResult;

/**
 * Http 요청 결과를 caller thread에게 돌려주는 역할.
 * 사용자가 등록한 callback을 callder thread에서 수행하도록 하기 위함
 * @param <T> : http 요청이 성공한 경우 return 객체 type. {@link #onHttpSuccess(T)}
 *           실패한 경우는 APIErrorResult. {@link #onHttpFailure(APIErrorResult)}
 * @author MJ
 */
public abstract class HttpResponseHandler<T> extends Handler {

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HttpRequestTask.SUCCESS:
                onHttpSuccess((T) msg.obj);
                break;
            case HttpRequestTask.ERROR:
                onHttpFailure((APIErrorResult) msg.obj);
                break;
            case HttpRequestTask.NEED_TO_LOGIN:
                onHttpSessionClosedFailure((APIErrorResult) msg.obj);
                break;
        }
    }

    /**
     * 요청한 request가 성공한 경우 호출된다.
     * @param resultObj 성공한 결과
     */
    protected abstract void onHttpSuccess(final T resultObj);

    /**
     * access token, refresh token이 모두 만료되어 다시 로그인이 필요한 경우 호출된다.
     * 다시 로그인 창으로 redirect하도록 한다.
     */
    protected abstract void onHttpSessionClosedFailure(final APIErrorResult errorResult);

    /**
     * request 요청을 완료했으나 서버에서 실패 결과를 준 경우 또는
     * request 요청을 완료하지 못했거나 완료했는데 결과처리 중에 문제가 발생한 경우 호출된다.
     * @param errorResult 실패한 결과
     */
    protected abstract void onHttpFailure(final APIErrorResult errorResult);
}
