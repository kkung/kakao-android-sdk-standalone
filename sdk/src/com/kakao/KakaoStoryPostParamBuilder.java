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
package com.kakao;

import android.os.Bundle;

import com.kakao.helper.ServerProtocol;

/*
 * Param Name	        Param Type	필수      설명
 * content	            String  	ㅇ       내용
 * permission	        enum	    o       A : 전체 공개   F : 친구 공개
 * image_url	        String  	X       이미지 URL
 * android_exec_param	String	    X       안드로이드 custom url에 붙일 파라미터 ${서버에 등록한 값} + "?" + ${android_exec_param}
 * ios_exec_param	    String	    X	    아이폰 custom url에 붙일 파라미터 ${서버에 등록한 값} + "?" + ${ios_exec_param}
*/
/**
 * 스토리 포스트시 필요한 정보를 구성하는 Builder이다.
 * 텍스트, 공개범위, 이미지, 앱연결시 추가 param.
 * 텍스트와 공개범위는 필수 항목이다.
 * @author MJ
*/
public class KakaoStoryPostParamBuilder {
    /**
     * 스토리 포스트시 공개 범위
     */
    public enum PERMISSION {
        /**
         * 전체 공개
         */
        PUBLIC("A"),
        /**
         * 친구 공개
         */
        FRIENDS("F");

        private final String value;

        PERMISSION(String value) {
            this.value = value;
        }
    }

    private final String content;
    private final PERMISSION permission;
    private String imageURL;
    private String androidExecuteParam;
    private String iosExecuteParam;

    /**
     * 친구 공개 포스팅 Builder를 만든다.
     * @param content 포스팅에 포함할 텍스트
     */
    public KakaoStoryPostParamBuilder(final String content) {
        this(content, PERMISSION.FRIENDS);
    }

    /**
     * 공개범위를 지정한 포스팅 Builder를 만든다.
     * @param content 포스팅에 포함할 텍스트
     * @param permission 포스팅 공개 여부
     */
    public KakaoStoryPostParamBuilder(final String content, final PERMISSION permission) {
        this.content = content;
        this.permission = permission;
    }

    /**
     * 포스팅에 이미지 추가한다.
     * @param imageURL 포스팅에 포함할 이미지 경로. upload API를 통해 얻은 결과
     * @return 이미지 추가 후 builder
     */
    public KakaoStoryPostParamBuilder setImageURL(final String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    /**
     * Android 앱연결 링크에 추가할 파라미터 설정한다.
     * 기본적으로는 kakao[appkey]://kakaostory로 연결되나 그 뒤에 파라미터를 붙이고 싶을 때 사용한다.
     * @param androidExecuteParam 추가할 파라미터
     * @return 파라미터 추가후 builder
     */
    public KakaoStoryPostParamBuilder setAndroidExecuteParam(final String androidExecuteParam) {
        this.androidExecuteParam = androidExecuteParam;
        return this;
    }
    /**
     * iOS 앱연결 링크에 추가할 파라미터 설정한다.
     * 기본적으로는 kakao[appkey]://kakaostory로 연결되나 그 뒤에 파라미터를 붙이고 싶을 때 사용한다.
     * @param iosExecuteParam 추가할 파라미터
     * @return 파라미터 추가후 builder
     */
    public KakaoStoryPostParamBuilder setIOSExecuteParam(final String iosExecuteParam) {
        this.iosExecuteParam = iosExecuteParam;
        return this;
    }

    /**
     * 지금까지 추가된 설정을 Bundle로 만들어준다.
     * @return 스토리 포스트 설정을 Bundle로 반환
     */
    public Bundle build(){
        final Bundle parameters = new Bundle();
        parameters.putString(ServerProtocol.CONTENT_KEY, content);
        parameters.putString(ServerProtocol.PERMISSION_KEY, permission.value);
        if(imageURL != null)
            parameters.putString(ServerProtocol.IMAGE_URL_KEY, imageURL);
        if(androidExecuteParam != null)
            parameters.putString(ServerProtocol.ANDROID_EXEC_PARAM_KEY, androidExecuteParam);
        if(iosExecuteParam != null)
            parameters.putString(ServerProtocol.IOS_EXEC_PARAM_KEY, iosExecuteParam);
        return parameters;
    }
}
