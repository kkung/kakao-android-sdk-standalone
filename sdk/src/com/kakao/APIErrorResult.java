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

/**
 * API 요청에 대한 오류 결과 객체
 * @author MJ
 */
public class APIErrorResult {
    /**
     * 에러를 일으킨 요청 URL
     */
    private String requestURL;
    // {@link com.kakao.helper.ServerProtocol.ERROR_CODE_KEY}와 같은 변수 이름 유지. for jackson parsing
    /**
     * 숫자로 구성된 에러 코드
     */
    protected int errorCode;

    //{@link com.kakao.helper.ServerProtocol.ERROR_MSG_KEY}와 같은 변수 이름 유지. for jackson parsing
    /**
     *  String으로 구성된 상세한 에러 메시지
     */
    protected String errorMessage;

    // for jackson
    public APIErrorResult(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public APIErrorResult(final String requestURL, final String errorMessage) {
        this.requestURL = requestURL;
        this.errorCode = ErrorCode.CLIENT_ERROR_CODE.getErrorCode();
        this.errorMessage = errorMessage;
    }

    /**
     * 에러를 일으킨 요청 URL을 넘겨준다.
     * @return 요청 URL
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * 에러를 일으킨 요청 URL을 설정 한다.
     * @param requestURL 요청 URL
     */
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    /**
     * 숫자로 구성된 에러 코드
     * @return 에러 코드
     */
    public int getErrorCodeInt() {
        return errorCode;
    }

    /**
     * Enum 에러 코드
     * @return {@link ErrorCode} 중 하나
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.valueOf(errorCode);
    }

    /**
     * String으로 구성된 상세한 에러 메시지
     * @return 에러 메시지
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 결과 객체를 String으로 표현
     * @return 요청 URL, 에러 코드, 에러 메시지를 포함한 string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("APIErrorResult{");
        sb.append("requestURL='").append(requestURL).append('\'');
        sb.append(", errorCode=").append(errorCode);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
