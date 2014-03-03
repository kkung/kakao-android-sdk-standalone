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
package com.kakao.authorization.authcode;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.kakao.helper.ServerProtocol;
import com.kakao.helper.Utility;

/**
 * @author MJ
 */
public class AuthorizationCode implements Parcelable {
    private final String authorizationCode;

    private AuthorizationCode(final String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public static AuthorizationCode createEmptyCode() {
        return new AuthorizationCode("");
    }

    public static AuthorizationCode createFromRedirectedUri(final Uri redirectedUri) {
        final String code = redirectedUri.getQueryParameter(ServerProtocol.CODE_KEY);
        return createNew(code);
    }

    private static AuthorizationCode createNew(final String authCode) {
        if (Utility.isNullOrEmpty(authCode)) {
            return createEmptyCode();
        } else {
            return new AuthorizationCode(authCode);
        }
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public boolean hasAuthorizationCode() {
        return !Utility.isNullOrEmpty(this.authorizationCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorizationCode);
    }

    public static final Parcelable.Creator<AuthorizationCode> CREATOR = new Parcelable.Creator<AuthorizationCode>() {
        public AuthorizationCode createFromParcel(Parcel in) {
            return new AuthorizationCode(in.readString());
        }

        public AuthorizationCode[] newArray(int size) {
            return new AuthorizationCode[size];
        }
    };
}
