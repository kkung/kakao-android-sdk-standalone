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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;

import com.kakao.authorization.authcode.AuthorizationCodeRequest;

/**
 * Talk과 통신하기 위한 protocol
 * authorization을 talk을 통해 하는 경우
 * kakaolink를 사용하는 경우
 *
 * @author MJ
 */
public class TalkProtocol {
    // capri login, kakaolink3.5
    private static final int TALK_MIN_VERSION_SUPPORT_CAPRI_100 = 139; // android 4.2.0
    private static final String INTENT_ACTION_LOGGED_OUT_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_OUT_ACTIVITY";
    private static final String INTENT_ACTION_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY";

    public static final int MESSAGE_GET_AUTH_CODE_REQUEST = 0x10000;
    public static final int MESSAGE_GET_AUTH_CODE_REPLY = 0x10001;

    //request
    private static final String EXTRA_PROTOCOL_VERSION = "com.kakao.sdk.talk.protocol.version";
    private static final int PROTOCOL_VERSION = 1;

    private static final String EXTRA_APPLICATION_KEY = "com.kakao.sdk.talk.appKey";
    private static final String EXTRA_REDIRECT_URI = "com.kakao.sdk.talk.redirectUri";

    //response
    public static final String EXTRA_REDIRECT_URL = "com.kakao.sdk.talk.redirectUrl";
    public static final String EXTRA_ERROR_DESCRIPTION = "com.kakao.sdk.talk.error.description";
    public static final String EXTRA_ERROR_TYPE = "com.kakao.sdk.talk.error.type";

    // error types
    public static final String NOT_SUPPORT_ERROR = "NotSupportError";
    public static final String ERROR_UNKNOWN_ERROR = "UnknownError";
    public static final String ERROR_PROTOCOL_ERROR = "ProtocolError";
    public static final String ERROR_APPLICATION_ERROR = "ApplicationError";
    public static final String ERROR_NETWORK_ERROR = "NetworkError";

    // kakolink
    public static final String KAKAO_TALK_LINK_URL = "kakaolink://send";

    private static final String RELEASE_TAlk_SIGNATURE = "308201db30820144a00302010202044c707197300d06092a864886f70d010105050030" +
        "31310b3009060355040613026b6f310e300c060355040a13056b616b616f31123010060355040b13096b616b616f7465616d3020170d" +
        "3130303832323030333834375a180f32313130303732393030333834375a3031310b3009060355040613026b6f310e300c060355040a" +
        "13056b616b616f31123010060355040b13096b616b616f7465616d30819f300d06092a864886f70d010101050003818d003081890281" +
        "8100aef387bc86e022a87e66b8c42153284f18e0c468cf9c87a241b989729dfdad3dd9e1847546d01a2819ba77f3974a47b473c926ac" +
        "ae173fd90c7e635000721feeef6705da7ae949a35b82900a0f67d9464d73ed8a98c37f4ac70729494a17469bc40d4ee06d043b09147e" +
        "badc55fa1020968d7036c5fb9b8c148cba1d8e9d9fc10203010001300d06092a864886f70d0101050500038181005569be704c68cff6" +
        "221c1e04dd8a131110f9f5cd2138042286337fd6014a1b1d2d3eeb266ae1630afe56bf63c07dd0b5c8fad46dcb9f802f9a7802fb89eb" +
        "3b4777b9665bb1ed9feaf1dc7cac4f91abedfc81187ff6d2f471dbd12335d2c0ef0e2ee719df6e763f814b9ac91f8be37fd11d406867" +
        "00d66be6de22a1836f060f01";

    private static final String DEBUG_TAlk_SIGNATURE = "308201e53082014ea00302010202044f4ae542300d06092a864886f70d01010505003037" +
        "310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f69642044656275" +
        "67301e170d3132303232373032303635385a170d3432303231393032303635385a3037310b30090603550406130255533110300e0603" +
        "55040a1307416e64726f6964311630140603550403130d416e64726f696420446562756730819f300d06092a864886f70d0101010500" +
        "03818d0030818902818100c0b41c25ef21a39a13ce89c82dc3a14bf9ef0c3094aa2ac1bf755c9699535e79119e8b980c0ecdcc51f259" +
        "eb0d8b2077d41de8fcfdeaac3f386c05e2a684ecb5504b660ad7d5a01cce35899f96bcbd099c9dcb274c6eb41fef861616a12fb45bc5" +
        "7a19683a8a97ab1a33d9c70128878b67dd1b3a388ad5121d1d66ff04c065ff0203010001300d06092a864886f70d0101050500038181" +
        "000418a7dacb6d13eb61c8270fe1fdd006eb66d0ff9f58f475defd8dc1fb11c41e34ce924531d1fd8ad26d9479d64f54851bf57b8dfe" +
        "3a5d6f0a01dcad5b8c36ac4ac48caeff37888c36483c26b09aaa9689dbb896938d5afe40135bf7d9f12643046301867165d28be0baa3" +
        "513a5084e182f7f9c044d5baa58bdce55fa1845241";

        public static Intent createLoggedOutActivityIntent(final Context context, final AuthorizationCodeRequest request) {
        Intent intent = new Intent()
                .setAction(INTENT_ACTION_LOGGED_OUT_ACTIVITY)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
                .putExtra(EXTRA_APPLICATION_KEY, request.getAppKey())
                .putExtra(EXTRA_REDIRECT_URI, request.getRedirectURI());
        return checkSupportedTalk(context, intent, TALK_MIN_VERSION_SUPPORT_CAPRI_100);
    }

    public static Intent createLoggedInActivityIntent(final Context context, final AuthorizationCodeRequest request) {
        Intent intent = new Intent()
                .setAction(INTENT_ACTION_LOGGED_IN_ACTIVITY)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
                .putExtra(EXTRA_APPLICATION_KEY, request.getAppKey())
                .putExtra(EXTRA_REDIRECT_URI, request.getRedirectURI());
        return checkSupportedTalk(context, intent, TALK_MIN_VERSION_SUPPORT_CAPRI_100);
    }


    public static Intent createKakakoTalkLinkIntent(final Context context, final String linkMessage) {
        final Uri kakaoLinkUri = Uri.parse(linkMessage);
        final Intent intent = new Intent(Intent.ACTION_SEND, kakaoLinkUri);
        // kakaolink를 지원하는 kakaotalk이 install 되어 있는지.
        return checkSupportedTalk(context, intent, TalkProtocol.TALK_MIN_VERSION_SUPPORT_CAPRI_100);
    }

    private static Intent checkSupportedTalk(final Context context, final Intent intent, final int minVersion) {
        if (intent == null) {
            return null;
        }

        // 해당 intent를 지원하는 kakaotalk인지 check
        ResolveInfo resolveInfo = Utility.resolveIntent(context, intent);
        if (resolveInfo == null) {
            return null;
        }

        // 설치된 kakaotalk이 변조된 것인지 check
        if (!validateTalkSignatureAndMinVersion(context, resolveInfo.activityInfo.packageName, minVersion)) {
            return null;
        }

        return intent;
    }

    private static boolean validateTalkSignatureAndMinVersion(final Context context, final String packageName, final int minVersion) {
        PackageInfo talkPackageInfo;
        try {
            talkPackageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        if(minVersion > 0){
            if(talkPackageInfo.versionCode < minVersion)
                return false;
        }

        for (Signature signature : talkPackageInfo.signatures) {
            String signatureCharsString = signature.toCharsString();
            if (signatureCharsString.equals(RELEASE_TAlk_SIGNATURE) ||
                signatureCharsString.equals(DEBUG_TAlk_SIGNATURE)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTalkProtocolMatched(final Intent data) {
        int protocolVersion = data.getIntExtra(EXTRA_PROTOCOL_VERSION, 0);
        return ((PROTOCOL_VERSION == protocolVersion));
    }
}
