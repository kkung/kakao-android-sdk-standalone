/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission.??
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

/**
 * @author MJ
 */
public final class ServerProtocol {
    public static enum DeployPhase {
        Local, Alpha, Sandbox, Beta, Release
    }

    private static final DeployPhase DEPLOY_PHASE = DeployPhase.Release;

    public static final String SDK_VERSION = "1.0.16";
    public static final String OS_ANDROID = "android";

    public static final String URL_SCHEME = "https";
    public static final String AUTH_AUTHORITY = initAuthAuthority();
    public static final String API_AUTHORITY = initAPIAuthority();

    //KAHeader
    public static final String KA_HEADER_KEY ="KA";
    public static final String KA_SDK_KEY ="sdk/";
    public static final String KA_OS_KEY ="os/";
    public static final String KA_LANG_KEY ="lang/";
    public static final String KA_RES_KEY ="res/";
    public static final String KA_DEVICE_KEY ="device/";

    //Authorization: Bearer
    public static final String AUTHORIZATION_HEADER_KEY ="Authorization";
    public static final String AUTHORIZATION_BEARER ="Bearer";

    // oauth url
    public static final String AUTHORIZE_CODE_PATH = "oauth/authorize";
    public static final String ACCESS_TOKEN_PATH = "oauth/token";

    //oauth param
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String RESPONSE_TYPE_KEY = "response_type";
    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String CODE_KEY = "code";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String EXPIRES_AT_KEY = "expires_in";
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String ANDROID_KEY_HASH = "android_key_hash";

    // oauth response
    public static final String ERROR_KEY = "error";
    public static final String ERROR_DESCRIPTION_KEY = "error_description";

    // api url
    private static final String API_VERSION = "v1";
    // usermgmt
    public static final String USER_ME_PATH = API_VERSION + "/user/me";
    public static final String USER_LOGOUT_PATH = API_VERSION + "/user/logout";
    public static final String USER_SIGNUP_PATH = API_VERSION + "/user/signup";
    public static final String USER_UNLINK_PATH = API_VERSION + "/user/unlink";
    public static final String USER_UPDATE_PROFILE_PATH = API_VERSION + "/user/update_profile";
    // story
    public static final String STORY_PROFILE_PATH = API_VERSION + "/api/story/profile";
    public static final String STORY_UPLOAD_PATH = API_VERSION + "/api/story/upload";
    public static final String STORY_POST_PATH = API_VERSION + "/api/story/post";
    //talk
    public static final String TALK_PROFILE_PATH = API_VERSION + "/api/talk/profile";


    //api response
    /**
     * {@link com.kakao.APIErrorResult} ????????? ?????? ????????? ???.
     */
    public static final String ERROR_CODE_KEY ="code";
    public static final String ERROR_MSG_KEY ="msg";

    // usermgmt param
    public static final String USER_ID_KEY ="id";
    public static final String PROPERTIES_KEY = "properties";
    /**
     * Policy.RESERVED_PROPERTY_NAMES ??? match
     */
    public static final String NICK_NAME_KEY = "nickname";
    public static final String PROFILE_THUMBNAIL_IMAGE_KEY = "thumbnail_image";
    public static final String PROFILE_IMAGE_KEY = "profile_image";

    // story param
    public static final String BODY_ENCODING ="UTF-8";
    public static final String FILE_KEY ="file";
    public static final String CONTENT_KEY ="content";
    public static final String PERMISSION_KEY ="permission";
    public static final String IMAGE_URL_KEY ="image_url";
    public static final String APP_CAPTION_TITLE_KEY ="app_caption_title";
    public static final String ANDROID_EXEC_PARAM_KEY ="android_exec_param";
    public static final String IOS_EXEC_PARAM_KEY ="ios_exec_param";

    private static String initAuthAuthority() {
        switch (DEPLOY_PHASE) {
            case Local:
                return "localhost:";
            case Alpha:
                return "alpha-kauth.kakao.com";
            case Sandbox:
                return "sandbox-kauth.kakao.com";
            case Beta:
                return "beta-kauth.kakao.com";
            case Release:
                return "kauth.kakao.com";
            default:
                return null;
        }
    }

    private static String initAPIAuthority() {
        switch (DEPLOY_PHASE) {
            case Local:
                return "localhost:";
            case Alpha:
                return "alpha-kapi.kakao.com";
            case Sandbox:
                return "sandbox-kapi.kakao.com";
            case Beta:
                return "beta-kapi.kakao.com";
            case Release:
                return "kapi.kakao.com";
            default:
                return null;
        }
    }

}
