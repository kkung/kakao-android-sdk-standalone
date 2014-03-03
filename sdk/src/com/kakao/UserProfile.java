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
import android.os.Parcel;
import android.os.Parcelable;

import com.kakao.helper.ServerProtocol;
import com.kakao.helper.SharedPreferencesCache;

import java.util.HashMap;
import java.util.Map;

// {@link com.kakao.helper.ServerProtocol.USER_ID_KEY}
// {@link com.kakao.helper.ServerProtocol.PROPERTIES_KEY}
/**
 * UserManagement API의 결과 객체로 사용자 id뿐 아니라 사용자 정보가 포함되어 있다.
 */
public class UserProfile implements Parcelable{
    private static final String CACHE_USER_PREFIX = "com.kakao.user.";
    private static final String CACHE_USER_ID = "com.kakao.user.userId";
    private static final String CACHE_NICKNAME = "com.kakao.user.nickname";
    private static final String CACHE_THUMBNAIL_PATH = "com.kakao.user.thumbbnailpath";
    private static final String CACHE_PROFILE_PATH = "com.kakao.user.profilepath";

    private final long id;
    private String nickname;
    private String thumbnailImagePath;
    private String profileImagePath;
    // predefined property를 제외한 사용자가 정의한 property
    private Map<String, String> properties = new HashMap<String, String>();

    private UserProfile(final long id, final String nickname, final String thumbnailImagePath, final String profileImagePath,
                       final Map<String, String> properties) {
        this.id = id;
        this.nickname = nickname;
        this.thumbnailImagePath = thumbnailImagePath;
        this.profileImagePath = profileImagePath;
        if(properties != null)
            this.properties = properties;
    }

    /**
     * UserManagement API 요청을 시도한 사용자 id
     *
     * @return UserManagement API 요청을 시도한 사용자 id
     */
    public long getId() {
        return id;
    }

    /**
     * 현재까지 저장되어 있는 사용자의 모든 정보를 key, value로 구성된 json type으로 반환
     * @return 앱에 저장된 사용자의 모든 정보
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * 앱 등록 당시 정의한 사용자의 정보 중 key에 해당하는 정보
     * @param propertyKey 알고 싶은 사용자 정보의 key
     * @return 해당 key의 정보
     */
    public String getProperty(final String propertyKey) {
        if(properties != null)
            return properties.get(propertyKey);
        else
            return null;
    }

    /**
     * 사용자 별명
     * @return 사용자 별명
     */
    public String getNickname() {
        if (nickname == null)
            return "undefined";
        else
            return nickname;
    }

    /**
     * 110px * 110px(톡에서 가지고 온 경우) 또는 160px * 160px(스토리에서 가지고 온 경우) 크기의 사용자의 썸네일 프로필 이미지 경로
     * @return 사용자의 썸네일 프로필 이미지 경로
     */
    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    /**
     * 480px * 480px ~ 1024px * 1024px 크기의 사용자의 프로필 이미지 경로
     * @return 사용자의 프로필 이미지 경로
     */
    public String getProfileImagePath() {
        return profileImagePath;
    }

    /**
     * 사용자의 프로필 정보를 String으로 변환한다
     * @return 사용자의 프로필 정보를 String으로 변환힌 값
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserProfile{");
        sb.append("nickname='").append(nickname).append('\'');
        sb.append(", thumbnailImagePath='").append(thumbnailImagePath).append('\'');
        sb.append(", profileImagePath='").append(profileImagePath).append('\'');
        sb.append(", properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 캐시로 부터 사용자정보를 읽어온다.
     * @return 캐시에서 읽은 사용자정보
     */
    public static UserProfile loadFromCache() {
        SharedPreferencesCache cache = Session.getAppCache();
        if(cache == null)
            return null;

        Bundle bundle = cache.load();

        final long userId = bundle.getLong(CACHE_USER_ID);
        bundle.remove(CACHE_USER_ID);
        final String nickname = bundle.getString(CACHE_NICKNAME);
        bundle.remove(CACHE_NICKNAME);
        final String thumbnailPath = bundle.getString(CACHE_THUMBNAIL_PATH);
        bundle.remove(CACHE_THUMBNAIL_PATH);
        final String profilePath = bundle.getString(CACHE_PROFILE_PATH);
        bundle.remove(CACHE_PROFILE_PATH);

        Map<String, String> properties = new HashMap<String, String>();
        if(!bundle.isEmpty()){
            for(String key : bundle.keySet()){
                if(key.startsWith(CACHE_USER_PREFIX))
                    properties.put(key, bundle.getString(key));
            }
        }

        return new UserProfile(userId, nickname, thumbnailPath, profilePath, properties);
    }

    /**
     * 요청 결과로 부터 UserProfile 객체를 만든다.
     * @param userProfileMap 사용자정보 요청결과 json으로 부터 얻은 Map
     * @return 요청 결과로 부터 만든 UserProfile 객체
     */
    public static UserProfile createFromResponse(final Map userProfileMap) {
        final Number userIdNumber = (Number) userProfileMap.get(ServerProtocol.USER_ID_KEY);
        final long userId = userIdNumber.longValue();

        final Map<String, String> properties = (Map<String, String>) userProfileMap.get(ServerProtocol.PROPERTIES_KEY);

        return createFromInput(userId, properties);
    }

    /**
     * 사용자정보 저장 요청 후 결과로 받은 사용자 id와 update한 사용자 정보로 부터 UserProfile 객체를 만든다.
     * @param userId 사용자 id
     * @param properties update한 사용자 정보
     * @return input으로부터 만든 UserProfile 객체
     */
    private static UserProfile createFromInput(final long userId, final Map<String, String> properties) {
        String nickname = null;
        String thumbnailPath = null;
        String profilePath = null;
        if (properties != null) {
            nickname = properties.remove(ServerProtocol.NICK_NAME_KEY);
            thumbnailPath = properties.remove(ServerProtocol.PROFILE_THUMBNAIL_IMAGE_KEY);
            profilePath = properties.remove(ServerProtocol.PROFILE_IMAGE_KEY);
        }
        return new UserProfile(userId, nickname, thumbnailPath, profilePath, properties);
    }

    /**
     * 원본 사용자정보와 update할 사용자 정보를 받아 원본에 update할 정보만 update한 결과를 준다.
     * @param originUserProfile 원본 사용자정보 객체
     * @param properties update할 사용자 정보
     * @return input을 merge한 결과
     */
    public static UserProfile updateUserProfile(final UserProfile originUserProfile, final Map<String, String> properties) {
        UserProfile userProfile = new UserProfile(originUserProfile.getId(), originUserProfile.getNickname(),
            originUserProfile.getThumbnailImagePath(), originUserProfile.getProfileImagePath(), originUserProfile.getProperties());
        if (properties != null) {
            final String nickname = properties.remove(ServerProtocol.NICK_NAME_KEY);
            if(nickname != null)
                userProfile.nickname = nickname;

            final String thumbnailPath = properties.remove(ServerProtocol.PROFILE_THUMBNAIL_IMAGE_KEY);
            if(thumbnailPath != null)
                userProfile.thumbnailImagePath = thumbnailPath;

            final String profilePath = properties.remove(ServerProtocol.PROFILE_IMAGE_KEY);
            if(profilePath != null)
                 userProfile.profileImagePath = profilePath;

            if(!properties.isEmpty())
                userProfile.properties.putAll(properties);
        }
        return userProfile;
    }

    /**
     * 사용자 객체를 캐시에 저장한다.
     */
    public void saveUserToCache() {
        SharedPreferencesCache cache = Session.getAppCache();
        if(cache == null)
            return;

        Bundle bundle = new Bundle();

        bundle.putLong(CACHE_USER_ID, id);
        bundle.putString(CACHE_NICKNAME, nickname);
        bundle.putString(CACHE_THUMBNAIL_PATH, thumbnailImagePath);
        bundle.putString(CACHE_PROFILE_PATH, profileImagePath);

        if(!properties.isEmpty()){
            for(String key : properties.keySet()){
                bundle.putString(CACHE_USER_PREFIX + key, properties.get(key));
            }
        }
        cache.save(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nickname);
        dest.writeString(thumbnailImagePath);
        dest.writeString(profileImagePath);
        dest.writeMap(properties);
    }

    public UserProfile(Parcel in) {
        id = in.readLong();
        nickname = in.readString();
        thumbnailImagePath = in.readString();
        profileImagePath = in.readString();
        in.readMap(properties, getClass().getClassLoader());
    }

    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

}
