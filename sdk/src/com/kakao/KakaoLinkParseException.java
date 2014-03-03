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
 * 카카오링크의 프로토콜에 맞지 않는 경우 던지는 exception
 * 
 * @author MJ
 */
public class KakaoLinkParseException extends Exception {
    private static final long serialVersionUID = 4539740978213889048L;
    private ERROR_CODE code = ERROR_CODE.UNKNOWN;

    /**
     * 카카오링크 프로토콜에 맞지 않는 경우 던지는 에러 코드
     */
    public enum ERROR_CODE {
        /**
         * 정의되지 않은 에러
         */
        UNKNOWN,
        /**
         * 필수 파라미터가 누락된 경우
         */
        CORE_PARAMETER_MISSING,
        /**
         * 최소 이미지 사이즈보다 작은 경우
         */
        MINIMUM_IMAGE_SIZE_REQUIRED,
        /**
         * 같은 타입의 메시지가 두개 이상 존재하는 경우
         */
        DUPLICATE_OBJECTS_USED,
        /**
         * 지원하지 않는 인코딩이 선언된 경우
         */
        UNSUPPORTED_ENCODING,
        /**
         * JSON 파싱 도중 에러
         */
        JSON_PARSING_ERROR
    }

    /**
     * exception을 던지는 이유 code값
     * @return {@link KakaoLinkParseException.ERROR_CODE} 중 하나
     */
    public ERROR_CODE getCode() {
        return code;
    }

    /**
     * exception을 던지는 이유 code 값과 에러 메시지
     * @return error_cod ":" error_message                            
     */
    public String getMessage(){
        return code != null ? code + ":" + super.getMessage() : super.getMessage();
    }

    KakaoLinkParseException(final String message) {
        super(message);
    }

    public KakaoLinkParseException(final ERROR_CODE code, final String e) {
        super(e);
        this.code = code;
    }

    KakaoLinkParseException(final ERROR_CODE code, final Exception e) {
        super(e);
        this.code = code;
    }

}
