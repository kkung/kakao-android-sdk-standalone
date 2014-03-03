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
package com.kakao.exception;

/**
 * web view 호출시 error
 */
public class KakaoWebviewException extends Throwable {
    private final String message;

    public KakaoWebviewException(final int errorCode, final String errorMessage, final String requestUrl) {
        StringBuilder message = new StringBuilder("code = ").append(errorCode);
        if(errorMessage != null)
            message.append(", msg = ").append(errorMessage);
        if(requestUrl != null)
            message.append(", url = ").append(requestUrl);
        this.message = message.toString();
    }

    public String getMessage(){
        return message;
    }
}
