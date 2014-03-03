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
package com.kakao.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.kakao.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utility {
    private static final String TAG = Utility.class.getCanonicalName();

    public static boolean isNullOrEmpty(final String s) {
        return (s == null) || (s.length() == 0);
    }

    public static Uri buildUri(final String authority, final String path) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ServerProtocol.URL_SCHEME);
        builder.authority(authority);
        builder.path(path);
        return builder.build();
    }

    public static Uri buildUri(final String authority, final String path, final Bundle parameters) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ServerProtocol.URL_SCHEME);
        builder.authority(authority);
        builder.path(path);
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            if (parameter instanceof String) {
                builder.appendQueryParameter(key, (String) parameter);
            }
        }
        return builder.build();
    }

    public static void putObjectInBundle(final Bundle bundle, final String key, final Object value) {
        if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) value);
        } else if (value instanceof byte[]) {
            bundle.putByteArray(key, (byte[]) value);
        } else {
            throw new KakaoException("attempted to add unsupported type to Bundle");
        }
    }

    public static void notNull(final Object arg, final String name) {
        if (arg == null) {
            throw new NullPointerException("Argument '" + name + "' cannot be null");
        }
    }

    public static String getMetadata(final Context context, final String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_META_DATA);
            if(ai == null)
                return null;
            else if(ai.metaData == null)
                return null;
            else
                return ai.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static ResolveInfo resolveIntent(final Context context, final Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0);
    }

    public static PackageInfo getPackageInfo(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to get PackageInfo", e);
        }
        return null;
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}

