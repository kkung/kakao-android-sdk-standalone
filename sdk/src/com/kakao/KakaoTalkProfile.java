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

import android.graphics.Bitmap;

/**
 * 카카오톡 프로필 요청의 결과 객체로
 * 카카오톡의 닉네임 정보(nickName), 카카오톡의 프로필 이미지 URL(profileImageURL), 카카오톡의 프로필 이미지의 썸네일 URL(thumbnailURL), 카카오톡의 국가코드(countryISO)로 구성되어 있다.
 * @author MJ
 */
public class KakaoTalkProfile {
    private String nickName;
    private String profileImageURL;
    private String thumbnailURL;
    private String countryISO;

    private Bitmap bgImageBitmap;

    // for jackson
    public KakaoTalkProfile(final String nickName, final String profileImageURL, final String thumbnailURL, final String countryISO) {
        this.nickName = nickName;
        this.profileImageURL = profileImageURL;
        this.thumbnailURL = thumbnailURL;
        this.countryISO = countryISO;
    }

    /**
     * 카카오톡 별명
     * @return 카카오톡 별명
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 640px * 640px 크기의 카카오톡 프로필 이미지 URL
     * @return 카카오톡 프로필 이미지 URL
     */
    public String getProfileImageURL() {
        return profileImageURL;
    }

    /**
     * 110px * 110px 크기의 카카오톡 썸네일 프로필 이미지 URL
     * @return 카카오톡 썸네일 프로필 이미지 URL
     */
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * 카카오톡 국가
     * @return 카카오톡 국가
     */
    public String getCountryISO() {
        return countryISO;
    }

    /**
     * 각 정보를 string으로 표현하여 반환한다.
     * @return 각 정보를 포함한 string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KakaoTalkProfile{");
        sb.append("nickName='").append(nickName).append('\'');
        sb.append(", profileImageURL='").append(profileImageURL).append('\'');
        sb.append(", thumbnailURL='").append(thumbnailURL).append('\'');
        sb.append(", countryISO='").append(countryISO).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
