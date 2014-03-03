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

import java.util.HashMap;
import java.util.Map;

// com.kakao.capri.api.exception.ApiErrorCodes와 sync
/**
 * API 요청에 대한 에러 코드
 */
 public enum ErrorCode {
    /**
     * 클라이언트 단에서 http 요청 전,후로 에러 발생한 경우. code = -777
     */
    CLIENT_ERROR_CODE(-777),
    /**
     * SDK가 인지 못하고 있는 에러코드
     */
    UNDEFINED_ERROR_CODE(-888),
    /**
     * 서버 내부에서 에러가 발생한 경우. code = -1
     */
    INTERNAL_ERROR_CODE(-1),
    /**
     * 올바르지 않은 파라미터가 전송된 경우. code = -2
     */
    INVALID_PARAM_CODE(-2),
    /**
     * [현재스펙에서는 아직 발생하지 않는다]
     * 카카오계정으로 로그인하지 않고 다른 타입의 계정으로 로그인한 사용자가 카카오서비스(카카오스토리,카카오톡) API를 호출한 경우 또는
     * 해당 API를 개발자 싸이트에서 disable 해놓은 경우. code = -3
     */
    NOT_SUPPORTED_API_CODE(-3),
    /**
     * 허용된 요청 회수가 초과한 경우로 자세한 내용은 쿼터 정책을 참고. code = -10
     */
    EXCEED_LIMIT_CODE(-10),
    /**
     * [로그인기반 API]
     * 해당 앱에 가입되지 않은 사용자가 호출한 경우 발생한다. code = -101
     */
    NOT_REGISTERED_USER_CODE(-101),
    /**
     * [사용자 관리 signup API]
     * 이미 해당 앱에 가입한 유저가 다시 가입 API를 요청한 경우 발생한다. code = -102
     */
    ALREADY_REGISTERED_USER_CODE(-102),
    /**
     * [카카오톡 API]
     * 존재하지 않는 카카오계정으로 요청한 경우 발생한다. code = -103
     */
    NOT_EXIST_KAKAO_ACCOUNT_CODE(-103),
    /**
     * [사용자 관리 me, signup, updateProfile API]
     * 앱에 추가하지 않은 사용자 프로퍼티 키의 값을 불러오거나 저장하려고 한 경우 발생한다.
     * 개발자의 앱 관리 페이지에 등록된 user property key의 이름이 잘못되지 않았는지 확인 필요하다. code = -201
     */
    NOT_REGISTERED_PROPERTY_KEY_CODE(-201),
    /**
     * 등록되지 않은 앱키 또는 앱키로 구성된 access token으로 요청한 경우 발생한다. code = -301
     */
    NOT_EXIST_APP_CODE(-301),
    /**
     * 앱 카테고리가 등록되지 않은 앱으로 요청한 경우 발생한다. code = -302
     */
    NOT_EXIST_APP_CATEGORY_CODE(-302),
    /**
     * [로그인기반 API]
     * 유효하지 않은 앱키 또는 access token으로 요청한 경우 발생한다. code = -401
     */
    INVALID_TOKEN_CODE(-401),
    /**
     * 해당 API에 대한 퍼미션이 없는 앱이 요청한 경우 발생한다. code = -402
     */
    INVALID_SCOPE_CODE(-402),
    /**
     * [카카오톡 API]
     * 카카오톡 미가입 사용자가 요청한 경우 발생한다. code = -501
     */
    NOT_EXIST_KAKAOTALK_USER_CODE(-501),
    /**
     * [카카오스토리 API]
     * 카카오스토리 미가입 사용자가 요청한 경우 발생한다. code = -601
     */
    NOT_EXIST_KAKAOSTORY_USER_CODE(-601),
    /**
     * [카카오스토리 upload API]
     * 카카오스토리 이미지 업로드시 5M 제한 크기를 넘을 경우한다.
     */
    EXCEED_MAX_UPLOAD_SIZE(-602),
    /**
     * 등록되지 않는 개발자의 앱키 또는 앱키로 구성된 access token으로 요청한 경우 발생한다. code = -701
     */
    NOT_EXIST_DEVELOPER_CODE(-701),
    /**
     * 카카오서비스가 점검중인 경우로 올바른 요청을 할 수 없는 경우 발생한다. code = -9798
     */
    KAKAO_MAINTENANCE_CODE(-9798);

    private final int errorCode;
    private static final Map<Integer, ErrorCode> reverseMap = new HashMap<Integer, ErrorCode>(17);

    static {
        for (ErrorCode errorCode : ErrorCode.values()) {
            reverseMap.put(errorCode.getErrorCode(), errorCode);
        }
    }

    ErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 숫자로 구성된 에러코드
     * @return 숫자로 구성된 에러코드
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 숫자 에러코드를 enum으로 변경하여 반환한다.
     * @param i 변경할 숫자 에러코드
     * @return 숫자에 해당하는 enum 에러코드
     */
    public static ErrorCode valueOf(final Integer i) {
        if(i == null)
            return null;
        ErrorCode errorCode = reverseMap.get(i);
        if(errorCode != null)
            return errorCode;
        else
            return UNDEFINED_ERROR_CODE;
    }
}
