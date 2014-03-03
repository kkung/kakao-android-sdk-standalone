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
package com.kakao.authorization.accesstoken;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kakao.helper.ServerProtocol;
import com.kakao.helper.SharedPreferencesCache;
import com.kakao.helper.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * refresh token에 대한 expires_at은 아직 내려오지 않는다.
 * @author MJ
 */
public class AccessToken implements Parcelable{
    private static final String CACHE_ACCESS_TOKEN = "com.kakao.token.AccessToken";
    private static final String CACHE_ACCESS_TOKEN_EXPIRES_AT = "com.kakao.token.AccessToken.ExpiresAt";
    private static final String CACHE_REFRESH_TOKEN = "com.kakao.token.RefreshToken";
    private static final String CACHE_REFRESH_TOKEN_EXPIRES_AT = "com.kakao.token.RefreshToken.ExpiresAt";

    private static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    private static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    private static final Date DEFAULT_EXPIRATION_TIME = MAX_DATE;
    private static final Date ALREADY_EXPIRED_EXPIRATION_TIME = MIN_DATE;

    private String accessTokenString;
    private String refreshTokenString;
    private Date accessTokenExpiresAt;
    private Date refreshTokenExpiresAt;

    public static AccessToken createEmptyToken() {
        return new AccessToken("", "", ALREADY_EXPIRED_EXPIRATION_TIME, ALREADY_EXPIRED_EXPIRATION_TIME);
    }

    public static AccessToken createFromCache(final Bundle bundle) {
        Utility.notNull(bundle, "bundle");
        final String accessToken = bundle.getString(CACHE_ACCESS_TOKEN);
        final String refreshToken = bundle.getString(CACHE_REFRESH_TOKEN);
        final Date accessTokenExpiresAt = SharedPreferencesCache.getDate(bundle, CACHE_ACCESS_TOKEN_EXPIRES_AT);
        final Date refreshTokenExpiresAt = SharedPreferencesCache.getDate(bundle, CACHE_REFRESH_TOKEN_EXPIRES_AT);

        return new AccessToken(accessToken, refreshToken, accessTokenExpiresAt, refreshTokenExpiresAt);
    }

    public static AccessToken createFromResponse(final Map resultObj) {
        String accessToken;
        long accessTokenExpiresAt;
        String refreshToken;
        //long refreshTokenExpiresAt =0L;

        accessToken = (String) resultObj.get(ServerProtocol.ACCESS_TOKEN_KEY);
        if(accessToken == null)
            return null;

        refreshToken = (String) resultObj.get(ServerProtocol.REFRESH_TOKEN_KEY);

        accessTokenExpiresAt = new Date().getTime() + (Integer)resultObj.get(ServerProtocol.EXPIRES_AT_KEY) * 1000;

        // 일단 refresh token의 expires_in은 나중에
        return new AccessToken(accessToken, refreshToken, new Date(accessTokenExpiresAt), MAX_DATE);
    }

    private AccessToken(final String accessTokenString, final String refreshTokenString, final Date accessTokenExpiresAt, final Date refreshTokenExpiresAt) {
        this.accessTokenString = accessTokenString;
        this.refreshTokenString = refreshTokenString;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public static void clearAccessTokenFromCache(final SharedPreferencesCache cache) {
        final List<String> keysToRemove = new ArrayList<String>();
        keysToRemove.add(CACHE_ACCESS_TOKEN);
        keysToRemove.add(CACHE_ACCESS_TOKEN_EXPIRES_AT);
        cache.clear(keysToRemove);
    }

    public void saveAccessTokenToCache(final SharedPreferencesCache cache) {
        Bundle bundle = new Bundle();

        bundle.putString(CACHE_ACCESS_TOKEN, accessTokenString);
        bundle.putString(CACHE_REFRESH_TOKEN, refreshTokenString);
        SharedPreferencesCache.putDate(bundle, CACHE_ACCESS_TOKEN_EXPIRES_AT, accessTokenExpiresAt);
        SharedPreferencesCache.putDate(bundle, CACHE_REFRESH_TOKEN_EXPIRES_AT, refreshTokenExpiresAt);

        cache.save(bundle);
    }

    public static boolean hasValidAccessToken(final Bundle bundle) {
        if(bundle == null)
            return false;

        String token = bundle.getString(CACHE_ACCESS_TOKEN);
        if(((token == null) || (token.length() == 0)))
            return false;

        Date expiresAt = SharedPreferencesCache.getDate(bundle, CACHE_ACCESS_TOKEN_EXPIRES_AT);
        return !(expiresAt == null || expiresAt.before(new Date()));

    }

    // refresh token
    public static boolean hasRefreshToken(final Bundle bundle) {
        Utility.notNull(bundle, "bundle");
        String token = bundle.getString(CACHE_REFRESH_TOKEN);
        return !((token == null) || (token.length() == 0));
    }

    // access token 갱신시에는 refresh token이 내려오지 않을 수도 있다.
    public void updateAccessToken(final AccessToken newAccessToken){
        String newRefreshToken = newAccessToken.refreshTokenString;
        if(TextUtils.isEmpty(newRefreshToken)){
            this.accessTokenString = newAccessToken.accessTokenString;
            this.accessTokenExpiresAt = newAccessToken.accessTokenExpiresAt;
        } else {
            this.accessTokenString = newAccessToken.accessTokenString;
            this.refreshTokenString = newAccessToken.refreshTokenString;
            this.accessTokenExpiresAt = newAccessToken.accessTokenExpiresAt;
            this.refreshTokenExpiresAt = newAccessToken.refreshTokenExpiresAt;
        }
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public String getRefreshTokenString() {
        return refreshTokenString;
    }

    public boolean hasRefreshToken(){
        return !Utility.isNullOrEmpty(this.refreshTokenString);
    }

    public boolean hasValidAccessToken() {
        return !Utility.isNullOrEmpty(this.accessTokenString) && !new Date().after(this.accessTokenExpiresAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accessTokenString);
        dest.writeString(refreshTokenString);
        dest.writeLong(accessTokenExpiresAt.getTime());
        dest.writeLong(refreshTokenExpiresAt.getTime());
    }

    public AccessToken(Parcel in) {
        accessTokenString = in.readString();
        refreshTokenString = in.readString();
        accessTokenExpiresAt = new Date(in.readLong());
        refreshTokenExpiresAt = new Date(in.readLong());
    }

    public static final Parcelable.Creator<AccessToken> CREATOR = new Parcelable.Creator<AccessToken>() {
        public AccessToken createFromParcel(Parcel in) {
            return new AccessToken(in);
        }

        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };

}
