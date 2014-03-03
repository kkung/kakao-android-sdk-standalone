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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.kakao.authorization.AuthorizationResult;
import com.kakao.authorization.accesstoken.AccessToken;
import com.kakao.authorization.accesstoken.AccessTokenRequest;
import com.kakao.authorization.authcode.AuthorizationCode;
import com.kakao.authorization.authcode.AuthorizationCodeRequest;
import com.kakao.exception.KakaoException;
import com.kakao.exception.KakaoException.ERROR_TYPE;
import com.kakao.helper.Logger;
import com.kakao.helper.SharedPreferencesCache;
import com.kakao.helper.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 로그인 상태를 유지 시켜주는 객체로 access token을 관리한다.
 * @author MJ
 */
public class Session {
    private static Session currentSession;
    private static final String APP_KEY_PROPERTY = "com.kakao.sdk.AppKey";
    private static final String REDIRECT_URL_PREFIX = "kakao";
    private static final String REDIRECT_URL_POSTFIX = "://oauth";


    private final Context context;
    private final String appKey;
    private final String redirectUri;
    private final SharedPreferencesCache appCache;
    private final List<SessionCallback> sessionCallbacks;
    private final Handler sessionCallbackHandler;

    private final Object INSTANCE_LOCK = new Object();
    // 아래 값들은 변경되는 값으로 INSTANCE_LOCK의 보호를 받는다.
    private SessionState state;
    // close시 삭제
    private RequestType requestType;
    private AuthorizationCode authorizationCode;
    private AccessToken accessToken;

    /**
     * 세션이 존재하지 않으면 세션을 생성하고, 기존에 존재하는데 만료되었으면 갱신을 시도한다.
     * 처음 세션을 접근할 때 사용한다.
     * opened 상태 : 다음 acitivity로
     * closed 상태 : 사용자 action을 받아 open 시도
     * opening 상태 : 토큰 갱신 시도
     * @param context 세션을 접근하는 context. 여기로 부터 app key와 redirect uri를 구해온다.
     * @param sessionCallback 토큰 갱신이 필요할 때 갱신의 결과를 받는 콜백
     */
    public static synchronized boolean initializeSession(final Context context, final SessionCallback sessionCallback) {
        if (currentSession == null) {
            currentSession = new Session(context);
        }
        return currentSession.implicitOpen(sessionCallback);
    }

    /**
     * 토큰 갱신이 가능한지 여부를 반환한다.
     * 토큰 갱신은 background로 사용자가 모르도록 진행한다.
     * @param sessionCallback 세션의 변경되었을 때 받게되는 콜백
     * @return 토큰 갱신을 진행할 때는 true, 토큰 갱신을 하지 못할때는 false를 return 한다.
     */
    public boolean implicitOpen(final SessionCallback sessionCallback) {
        if (currentSession.isOpening() && currentSession.accessToken.hasRefreshToken()) {
            open(sessionCallback);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 현재 세션을 반환한다.
     * @return 현재 세션 객체
     */
    public static synchronized Session getCurrentSession() {
        if(currentSession == null)
            throw new IllegalStateException("Session is not initialized. Use Session#initializeSession(Context ,SessionCallback) in login process.");
        return currentSession;
    }

    /**
     * 세션 오픈을 진행한다.
     * {@link SessionState#OPENED} 상태이면 바로 종료.
     * {@link SessionState#CLOSED} 상태이면 authorization code 요청. 에러/취소시 {@link SessionState#CLOSED}
     * {@link SessionState#OPENING} 상태이면 code 또는 refresh token 이용하여  access token 을 받아온다. 에러/취소시 {@link SessionState#CLOSED}, refresh 취소시에만 {@link SessionState#OPENING} 유지.
     * param으로 받은 콜백으로 그 결과를 전달한다.
     * @param sessionCallback 오픈 결과를 받은 콜백
     */
    public void open(final SessionCallback sessionCallback) {
        // 이미 open이 되어 있다.
        if (getState().isOpened()) {
            return;
        }

        addCallback(sessionCallback);

        //끝나지 않은 request가 있다.
        if(getRequestType() != null){
            Logger.getInstance().d(getRequestType() + " is still doing.");
            return;
        }
        try {
            checkLoginActivity();
            synchronized (INSTANCE_LOCK){
                switch (state) {
                    case CLOSED:
                        if (appKey != null && redirectUri != null) {
                            this.requestType = RequestType.GETTING_AUTHORIZATION_CODE;
                            final AuthorizationCodeRequest authorizationCodeRequest = AuthorizationCodeRequest.createNewRequest(appKey, redirectUri);
                            requestLogin(getLoginActivityIntent(authorizationCodeRequest));
                        } else {
                            internalClose(new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "can not request authorization code because appKey or redirectUri is invalid."), false);
                        }
                        break;
                    case OPENING:
                        if(accessToken.hasRefreshToken()){
                            this.requestType = RequestType.REFRESHING_ACCESS_TOKEN;
                            final  AccessTokenRequest accessTokenRequest = AccessTokenRequest.createRequestWithRefreshToken(context, appKey, redirectUri, accessToken.getRefreshTokenString());
                            requestLogin(getLoginActivityIntent(accessTokenRequest));
                        } else if(authorizationCode.hasAuthorizationCode()){
                            this.requestType = RequestType.GETTING_ACCESS_TOKEN;
                            final AccessTokenRequest accessTokenRequest = AccessTokenRequest.createRequestWithAuthorizationCode(context, appKey, redirectUri, authorizationCode.getAuthorizationCode());
                            requestLogin(getLoginActivityIntent(accessTokenRequest));
                        } else {
                            internalClose(new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "can not request access token because both authorization code and refresh token are invalid."), false);
                        }
                        break;
                    default:
                        throw new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "current session state is not possible to open. state = " + state);
                }
            }
        } catch (KakaoException e) {
            internalClose(e, false);
        }
    }

    /**
     * 명시적 강제 close(로그아웃/탈퇴). request중 인 것들은 모두 실패를 받게 된다.
     * token을 삭제하기 때문에 authorization code부터(로그인 버튼) 다시 받아서 세션을 open 해야한다.
     * @param sessionCallback close 결과를 받고자 하는 callback
     */
    public void close(final SessionCallback sessionCallback) {
        if(sessionCallback != null)
            addCallback(sessionCallback);

        internalClose(null, true);
    }

    /**
     * 현재 세션이 가지고 있는 access token이 유효한지를 검사후 세션의 상태를 반환한다.
     * 만료되었다면 opened 상태가 아닌 opening상태가 반환된다.
     * @return 세션의 상태
     */
    public final SessionState checkState() {
        synchronized (INSTANCE_LOCK) {
            if(state.isOpened() && !accessToken.hasValidAccessToken()){
                synchronized (INSTANCE_LOCK) {
                    state = SessionState.OPENING;
                    requestType = null;
                    authorizationCode = AuthorizationCode.createEmptyCode();
                }
            }
            return state;
        }
    }

    /**
     * 현재 세션의 상태
     * @return 세션의 상태
     */
    public SessionState getState() {
        synchronized (INSTANCE_LOCK) {
            return state;
        }
    }

    /**
     * 현재 세션이 열린 상태인지 여부를 반환한다.
     * @return 세션이 열린 상태라면 true, 그외의 경우 false를 반환한다.
     */
    public final boolean isOpened() {
        final SessionState state = checkState();
        return state == SessionState.OPENED;
    }

    private boolean isOpening() {
        final SessionState state = checkState();
        return state == SessionState.OPENING;
    }

    /**
     * 현재 세션이 닫힌 상태인지 여부를 반환한다.
     * @return 세션이 닫힌 상태라면 true, 그외의 경우 false를 반환한다.
     */
    public final boolean isClosed() {
        final SessionState state = checkState();
        return state == SessionState.CLOSED;
    }

    /**
     * 현재 진행 중인 요청 타입
     * @return 현재 진행 중인 요청 타입
     */
    public final RequestType getRequestType() {
        synchronized (INSTANCE_LOCK) {
            return requestType;
        }
    }

    /**
     * 현재 세션이 가지고 있는 access token을 반환한다.
     * @return access token
     */
    public final String getAccessToken() {
        synchronized (INSTANCE_LOCK) {
            return (accessToken == null) ? null : accessToken.getAccessTokenString();
        }
    }

    /**
     * 앱 캐시를 반환한다.
     * @return 앱 캐시
     */
    public static SharedPreferencesCache getAppCache() {
        final Session session = Session.getCurrentSession();
        return session.appCache;
    }

    /**
     * authorization code 결과를 받아 처리한다. (authcode 저장, state 변경, accesstoken요청)
     */
    public void onAuthCodeCompleted(final AuthorizationResult result) {
        AuthorizationCode authCode = null;
        KakaoException exception = null;
        if(getRequestType() == null){
            exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "session is closed during requesting authorization code. result will be ignored. state = " + getState());
        } else if (result == null) {
            exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "the result of authorization code request is null.");
        } else {
            final String resultRedirectURL = result.getRedirectURL();
            if(result.isSuccess()){
                // 기대 했던 redirect uri 일치
                if (resultRedirectURL != null && resultRedirectURL.startsWith(redirectUri)) {
                    authCode = AuthorizationCode.createFromRedirectedUri(result.getRedirectUri());
                    // authorization code가 포함되지 않음
                    if (!authCode.hasAuthorizationCode()) {
                        authCode = null;
                        exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "the result of authorization code request does not have authorization code.");
                    }
                // 기대 했던 redirect uri 불일치
                } else {
                    exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "the result of authorization code request mismatched the registered redirect uri. msg = " + result.getResultMessage());
                }
            } else if (result.isCanceled()) {
                exception = new KakaoException(ERROR_TYPE.CANCELED_OPERATiON, result.getResultMessage());
            } else {
                exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, result.getResultMessage());
            }
        }

        synchronized (INSTANCE_LOCK) {
            final SessionState previousState = state;
            if (authCode != null) {
                this.authorizationCode = authCode;
                state = SessionState.OPENING;
                // log만 남기고 callback은 호출되지 않는다.
                onStateChange(previousState, state, requestType, null, false);
                //  request가 성공적으로 끝났으니 request는 reset
                requestType = null;
            } else {
                internalClose(exception, false);
                return;
            }
        }
        // request AccessToken
        open(null);
    }

    /**
     * access token 결과를 받아 처리한다. (access token 저장, state 변경)
     */
    public void onAccessTokenCompleted(final AuthorizationResult result) {
        AccessToken resultAccessToken = null;
        KakaoException exception = null;
        if(getRequestType() == null){
            exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "session is closed during requesting access token. result will be ignored. state = " + getState());
        } else if (result == null) {
            exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "the result of access token request is null.");
        } else {
            if (result.isSuccess()) {
                resultAccessToken = result.getAccessToken();
                if (!resultAccessToken.hasValidAccessToken()) {
                    resultAccessToken = null;
                    exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "the result of access token request is invalid access token.");
                }
            } else if (result.isCanceled()) {
                exception = new KakaoException(ERROR_TYPE.CANCELED_OPERATiON, result.getResultMessage());
            } else {
                exception = new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, result.getResultMessage());
            }
        }

        synchronized (INSTANCE_LOCK) {
            final SessionState previousState = state;
            if (resultAccessToken != null) {
                // refresh 요청에는 refresh token이 내려오지 않을 수 있으므로 accessToken = resultAccessToken을 하면 안된다.
                accessToken.updateAccessToken(resultAccessToken);
                //authorization code는 한번 밖에 사용하지 못한다.
                authorizationCode = AuthorizationCode.createEmptyCode();
                saveTokenToCache(accessToken);
                state = SessionState.OPENED;
                onStateChange(previousState, state, requestType, null, false);
                requestType = null;
            } else {
                // refresh token으로 요청했는데 취소를 한 경우는 다음에 다시 refresh token을 사용할 수 있으므로 close시키진 않는다.
                if(!(getRequestType().isRefreshingTokenRequest() && exception.isCancledOperation())){
                    internalClose(exception, false);
                }
            }
        }
    }

    private Session(final Context context){
        if(context == null)
            throw new KakaoException(ERROR_TYPE.ILLEGAL_ARGUMENT, "cannot create Session without Context.");

        this.context = context;
        this.appKey = Utility.getMetadata(context, APP_KEY_PROPERTY);
        if(appKey == null)
            throw new KakaoException(ERROR_TYPE.MISS_CONFIGURATION, String.format("need to declare %s in your AndroidManifest.xml", APP_KEY_PROPERTY));
        this.redirectUri = REDIRECT_URL_PREFIX + this.appKey + REDIRECT_URL_POSTFIX;
        this.appCache = new SharedPreferencesCache(context, appKey);
        this.sessionCallbacks = new ArrayList<SessionCallback>();
        this.sessionCallbackHandler = new Handler(Looper.getMainLooper()); //세션 callback은 main thread에서 호출되도록 한다.

        final Bundle loadedFromCache = appCache.load();
        synchronized (INSTANCE_LOCK) {
            authorizationCode = AuthorizationCode.createEmptyCode();
            accessToken = AccessToken.createFromCache(loadedFromCache);
            if (accessToken.hasValidAccessToken()) {
                this.state = SessionState.OPENED;
            } else if (accessToken.hasRefreshToken()) {
                this.state = SessionState.OPENING;
            } else {
                this.state = SessionState.CLOSED;
                internalClose(null, false);
            }
        }
    }

    /**
     * @param callback 추가할 세션 콜백
     */
    private void addCallback(final SessionCallback callback) {
        synchronized (sessionCallbacks) {
            if (callback != null && !sessionCallbacks.contains(callback)) {
                sessionCallbacks.add(callback);
            }
        }
    }

    private void removeCallbacks(final List<SessionCallback> sessionCallbacksToBeRemoved) {
        synchronized (sessionCallbacks) {
            sessionCallbacks.removeAll(sessionCallbacksToBeRemoved);
        }
    }

    private void checkLoginActivity() throws KakaoException {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        if (!resolveIntent(intent)) {
            throw new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, String.format("need to declare %s as an activity in your AndroidManifest.xml",
                LoginActivity.class.getName()));
        }
    }

    private boolean resolveIntent(final Intent intent) {
        final ResolveInfo resolveInfo = Utility.resolveIntent(context, intent);
        return resolveInfo != null;
    }

    private Intent getLoginActivityIntent(final AuthorizationCodeRequest authCodeRequest) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.CODE_REQUEST_KEY, authCodeRequest);
        return intent;
    }

    private Intent getLoginActivityIntent(final AccessTokenRequest accessTokenRequest) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.TOKEN_REQUEST_KEY, accessTokenRequest);
        return intent;
    }

    private void requestLogin(final Intent intent) {
        boolean found = startLoginActivity(intent);
        if (!found) {
            internalClose(new KakaoException(ERROR_TYPE.AUTHORIZATION_FAILED, "failed to find LoginActivity"), false);
        }
    }

    private boolean startLoginActivity(final Intent intent) {
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * 세션을 close하여 처음부터 새롭게 세션 open을 진행한다.
     * @param kakaoException  exception이 발생하여 close하는 경우 해당 exception을 넘긴다.
     * @param forced 강제 close 여부. 강제 close이면 이미 close가 되었어도 callback을 호출한다.
     */
    private void internalClose(final KakaoException kakaoException, final boolean forced) {
        synchronized (INSTANCE_LOCK) {
            final SessionState previous = state;
            state = SessionState.CLOSED;
            requestType = null;
            authorizationCode = AuthorizationCode.createEmptyCode();
            accessToken = AccessToken.createEmptyToken();
            onStateChange(previous, state, requestType, kakaoException, forced);
        }
        if (this.appCache != null) {
            this.appCache.clearAll();
        }
        // 해당 도메인 cookie만 지우려고 했으나 CookieManager가 관리하는 cookie가 한 app에 대한 cookie여서 모두 날려도 되겠다.
        // CookieManager를 쓰려면 CookieSyncManager를 만들어야 하는 버그가 있다.
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
    }

    private void saveTokenToCache(final AccessToken newToken) {
        if (newToken != null && appCache != null) {
            newToken.saveAccessTokenToCache(appCache);
        }
    }

    private void onStateChange(final SessionState previousState, final SessionState newState,
                               final RequestType requestType, final KakaoException exception,
                               boolean forced) {
        if (!forced && (previousState == newState) && exception == null) {
            return;
        }

        Logger.getInstance().d(String.format("Session State changed : %s -> %s \n ex = %s, request_type = %s",previousState, newState ,(exception != null ? ", ex=" + exception.getMessage() : ""), requestType));

        // 사용자에게 opening을 state를 알려줄 필요는 없는듯.
        if( newState.isOpening())
            return;

        final List<SessionCallback> dumpSessionCallbacks = new ArrayList<SessionCallback>(sessionCallbacks);
        Runnable runCallbacks = new Runnable() {
            public void run() {
                for(SessionCallback callback : dumpSessionCallbacks){
                    if (newState.isOpened())
                        callback.onSessionOpened();
                    else if (newState.isClosed())
                        callback.onSessionClosed(exception);

                }
                removeCallbacks(dumpSessionCallbacks);
            }
        };
        //세션 callback은 main thread에서 호출되도록 한다.
        sessionCallbackHandler.post(runCallbacks);
    }

    /**
     * @author MJ
     */
    private static enum SessionState {
        /**
         * memory와 cache에 session 정보가 없는 전혀 상태.
         * 처음 session에 접근할 때 또는 session을 close(예를 들어 로그아웃, 탈퇴)한 상태.
         * open({@link com.kakao.Session.RequestType#GETTING_AUTHORIZATION_CODE}) : 성공 - {@link #OPENING}, 실패 - 그대로 CLOSED
         * close(명시적 close) : 그대로 CLOSED
         */
        CLOSED,
        /**
         * {@link #CLOSED}상태에서 token을 발급 받기 위해 authorization code를 발급 받아 valid한 authorization code를 가지고 있는 상태.
         * 또는 토큰이 만료되었으나 refresh token을 가지고 있는 상태.
         * open({@link com.kakao.Session.RequestType#GETTING_ACCESS_TOKEN} 또는 {@link com.kakao.Session.RequestType#REFRESHING_ACCESS_TOKEN}) :  성공 - {@link #OPENED}, 실패 - {@link #CLOSED}
         * close(명시적 close) : {@link #CLOSED}
         */
        OPENING,
        /**
         * access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태.
         * 토크 만료 : {@link #OPENING}
         * close(명시적 close) : {@link #CLOSED}
         */
        OPENED;

        private boolean isClosed(){
            return this == SessionState.CLOSED;
        }

        private boolean isOpening(){
            return this == SessionState.OPENING;
        }

        private boolean isOpened(){
            return this == SessionState.OPENED;
        }
    }

    private enum RequestType {
        GETTING_AUTHORIZATION_CODE,
        GETTING_ACCESS_TOKEN,
        REFRESHING_ACCESS_TOKEN;

        private boolean isAuthorizationCodeRequest() {
            return this == RequestType.GETTING_AUTHORIZATION_CODE;
        }

        private boolean isAccessTokenRequest() {
            return this == RequestType.GETTING_ACCESS_TOKEN;
        }

        private boolean isRefreshingTokenRequest() {
            return this == RequestType.REFRESHING_ACCESS_TOKEN;
        }
    }
}
