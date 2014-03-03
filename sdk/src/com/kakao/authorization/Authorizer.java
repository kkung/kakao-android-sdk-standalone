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
package com.kakao.authorization;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.Serializable;

/**
 * @author MJ
 */
public abstract class Authorizer implements Serializable {
    private static final long serialVersionUID = -601355608597936016L;
    protected transient Activity loginActivity;
    private transient StartActivityDelegate startActivityDelegate;
    private transient boolean hasInternetPermission;
    protected transient OnAuthorizationListener onAuthorizationListener;
    private transient BackgroundProcessingListener backgroundProcessingListener;

    public interface OnAuthorizationListener {
        public void onAuthorizationCompletion(AuthorizationResult result);
    }

    public interface BackgroundProcessingListener {
        void onBackgroundProcessingStarted();

        void onBackgroundProcessingStopped();
    }

    public interface StartActivityDelegate {
        public void startActivityForResult(Intent intent, int requestCode);

        public Activity getActivityContext();
    }

    protected boolean checkInternetPermission() {
        if (hasInternetPermission) {
            return true;
        }

        int permissionCheck = loginActivity.checkCallingOrSelfPermission(Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            doneOnError("This Operation needs INTERNET permission.");
            return false;
        } else {
            hasInternetPermission = true;
            return true;
        }
    }

    protected abstract void completed(AuthorizationResult outcome);

    protected abstract void doneOnError(final String resultMessage);

    public void setLoginActivity(final Activity loginActivity) {
        this.loginActivity = loginActivity;
        startActivityDelegate = new StartActivityDelegate() {
            @Override
            public void startActivityForResult(Intent intent, int requestCode) {
                loginActivity.startActivityForResult(intent, requestCode);
            }

            @Override
            public Activity getActivityContext() {
                return loginActivity;
            }
        };
    }

    OnAuthorizationListener getOnAuthorizationListener() {
        return onAuthorizationListener;
    }

    public void setOnAuthorizationListener(OnAuthorizationListener onAuthorizationListener) {
        this.onAuthorizationListener = onAuthorizationListener;
    }

    BackgroundProcessingListener getBackgroundProcessingListener() {
        return backgroundProcessingListener;
    }

    public void setBackgroundProcessingListener(BackgroundProcessingListener backgroundProcessingListener) {
        this.backgroundProcessingListener = backgroundProcessingListener;
    }

    public void notifyBackgroundProcessingStart() {
        if (backgroundProcessingListener != null) {
            backgroundProcessingListener.onBackgroundProcessingStarted();
        }
    }

    public void notifyBackgroundProcessingStop() {
        if (backgroundProcessingListener != null) {
            backgroundProcessingListener.onBackgroundProcessingStopped();
        }
    }

    public StartActivityDelegate getStartActivityDelegate() {
        return startActivityDelegate;
    }
}
