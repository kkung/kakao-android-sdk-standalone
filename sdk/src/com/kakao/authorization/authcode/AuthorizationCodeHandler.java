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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.authorization.AuthorizationResult;
import com.kakao.helper.TalkProtocol;

/**
 * @author MJ
 */
abstract class AuthorizationCodeHandler {
    final GetterAuthorizationCode authorizer;

    AuthorizationCodeHandler(final GetterAuthorizationCode authorizer){
        this.authorizer = authorizer;
    }

    public abstract boolean tryAuthorize(final AuthorizationCodeRequest request);

    public void cancel() {}

    public boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        AuthorizationResult outcome;

        if (data == null) {
            // This happens if the user presses 'Back'.
            outcome = AuthorizationResult.createAuthCodeCancelResult("pressed back button or cancel button during requesting auth code.");
        } else if (TalkProtocol.isTalkProtocolMatched(data)) {
            outcome = AuthorizationResult.createAuthCodeErrorResult("TalkProtocol is mismatched during requesting auth code through KakaoTalk.");
        } else if (resultCode == Activity.RESULT_CANCELED) {
            outcome = AuthorizationResult.createAuthCodeCancelResult("pressed cancel button during requesting auth code.");
        } else if (resultCode != Activity.RESULT_OK) {
            outcome = AuthorizationResult.createAuthCodeErrorResult("got unexpected resultCode during requesting auth code. code=" + requestCode);
        } else {
            outcome = handleResultOk(data);
        }

        if(outcome.isPass())
            authorizer.tryNextHandler();
        else
            authorizer.completed(outcome);

        return true;
    }

    private AuthorizationResult handleResultOk(final Intent data) {
        Bundle extras = data.getExtras();
        String errorType = extras.getString(TalkProtocol.EXTRA_ERROR_TYPE);
        String rediretURL = extras.getString(TalkProtocol.EXTRA_REDIRECT_URL);
        if (errorType == null && rediretURL != null) {
            return AuthorizationResult.createSuccessAuthCodeResult(rediretURL);
        } else {
            if(errorType != null && errorType.equals(TalkProtocol.NOT_SUPPORT_ERROR))
                return AuthorizationResult.createAuthCodePassResult();
            String errorDes = extras.getString(TalkProtocol.EXTRA_ERROR_DESCRIPTION);
            return AuthorizationResult.createAuthCodeErrorResult("redirectURL=" + rediretURL +", " + errorType + " : " + errorDes);
        }
    }

    public boolean needsInternetPermission() {
        return true;
    }
}
