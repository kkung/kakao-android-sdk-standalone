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
import com.kakao.helper.Logger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 카카오스토리 프로필 요청의 결과 객체로
 * 카카오스토리의 닉네임 정보 (nickName), 카카오스토리의 생일 (birthday), 카카오스토리의 생일 타입 (birthdayType), 카카오스토리의 프로필 이미지 URL (profileImageURL), 카카오스토리의 프로필 이미지의 썸네일 URL (thumbnailURL), 카카오스토리의 배경 이미지 URL (bgImageURL)로 구성되어 있다.
 * @author MJ
 */
public class KakaoStoryProfile {
    private String nickName;
    private String profileImageURL;
    private String thumbnailURL;
    private String bgImageURL;
    private String birthday;
    private BirthdayType birthdayType;

    private Bitmap bgImageBitmap;
    private Calendar birthdayCalendar;

    public KakaoStoryProfile(String nickName, String profileImageURL, String thumbnailURL, String bgImageURL, String birthday, BirthdayType birthdayType) {
        this.nickName = nickName;
        this.profileImageURL = profileImageURL;
        this.thumbnailURL = thumbnailURL;
        this.bgImageURL = bgImageURL;
        this.birthday = birthday;
        this.birthdayType = birthdayType;
    }

    /**
     * 카카오스토리의 생일 타입
     */
    public enum BirthdayType {
        /**
         * 양력 생일
         */
        SOLAR("+"),
        /**
         * 음력 생일
         */
        LUNAR("-");
        private final String displaySymbol;

        BirthdayType(String s) {
            this.displaySymbol = s;
        }

        /**
         * 양력 생일은 '+', 음력 생일은 '-'로 반환한다.
         * @return '+','-'로 생일 타입을 반환한다.
         */
        public String getDisplaySymbol() {
            return displaySymbol;
        }
    }

    // for jackson
    public KakaoStoryProfile() {
    }

    /**
     * 카카오스토리 별명
     * @return 카카오스토리 별명
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 카카오스토리 프로필 이미지 URL
     * @return 카카오스토리 프로필 이미지 URL
     */
    public String getProfileImageURL() {
        return profileImageURL;
    }

    /**
     * 160px * 160px 크기의 카카오스토리 썸네일 프로필 이미지 URL
     * @return 카카오스토리 썸네일 프로필 이미지 URL
     */
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 카카오스토리 배경 이미지 URL
     * @return 카카오스토리 배경 이미지 URL
     */
    public String getBgImageURL() {
        return bgImageURL;
    }

    /**
     * 카카오스토리 생일 MMdd 형태
     * @return 카카오스토리 생일 MMdd 형태
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * 카카오스토리 생일을 Calendar 타입
     * @return 카카오스토리 생일을 Calendar 형식으로 반환
     */
    public Calendar getBirthdayCalendar() {
        return birthdayCalendar;
    }

    /**
     * 카카오스토리 생일 타입. 양력 또는 음력
     * @return 카카오스토리 생일 타입. 양력 또는 음력
     */
    public BirthdayType getBirthdayType() {
        return birthdayType;
    }

    /**
     * jackson에서 객체를 만들 때 사용한다.
     * @param birthday MMdd 형태의 생일 string 값
     */
    public void setBirthday(final String birthday) {
        if(birthday == null)
            return;
        final SimpleDateFormat form = new SimpleDateFormat("MMdd");
        birthdayCalendar  = Calendar.getInstance();
        try {
            birthdayCalendar.setTime(form.parse(birthday));
        } catch (java.text.ParseException e) {
            Logger.getInstance().d(e);
        }
        this.birthday = birthday;
    }

    /**
     * 각 정보를 string으로 표현하여 반환한다.
     * @return 각 정보를 포함한 string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KakaoStoryProfile{");
        sb.append("nickName='").append(nickName).append('\'');
        sb.append(", profileImageURL='").append(profileImageURL).append('\'');
        sb.append(", thumbnailURL='").append(thumbnailURL).append('\'');
        sb.append(", bgImageURL='").append(bgImageURL).append('\'');
        sb.append(", birthday='").append(birthday).append('\'');
        sb.append(", birthdayType=").append(birthdayType);
        sb.append('}');
        return sb.toString();
    }
}
