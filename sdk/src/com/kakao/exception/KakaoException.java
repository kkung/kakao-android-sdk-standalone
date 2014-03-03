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
 * general exception
 */
public class KakaoException extends RuntimeException {
    private static final long serialVersionUID = 3738785191273730776L;
    private ERROR_TYPE errorType;

    public enum ERROR_TYPE{
        ILLEGAL_ARGUMENT,
        MISS_CONFIGURATION,
        CANCELED_OPERATiON,
        AUTHORIZATION_FAILED,
    }

    public KakaoException(final ERROR_TYPE errorType, final String msg) {
        super(msg);
        this.errorType = errorType;
    }

    public boolean isCancledOperation() {
        return errorType == ERROR_TYPE.CANCELED_OPERATiON;
    }

    public KakaoException(final String msg) {
        super(msg);
    }

    public KakaoException(final Throwable t) {
        super(t);
    }

    public KakaoException(final String s, final Throwable t) {
        super(s, t);
    }

    public String getMessage(){
        if(errorType != null)
            return errorType.toString() +" : " + super.getMessage();
        else
            return super.getMessage();
    }
}
