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
package com.kakao.internal;

import java.nio.charset.Charset;

/**
 * @author MJ
 */
public final class KakaoTalkLinkProtocol {
    public static final String LINK_VERSION = "3.5";
    public static final String API_VERSION = "3.0";
    public static final String ENCODING = Charset.forName("UTF-8").name();

    // main key
    public static final String APP_KEY = "appkey";
    public static final String APP_VER = "appver";
    public static final String API_VER = "apiver";
    public static final String LINK_VER = "linkver";
    public static final String OBJS = "objs";

    // obj의 element
    static final String OBJ_OBJTYPE = "objtype";
    static final String OBJ_TEXT = "text";
    static final String OBJ_SRC = "src";
    static final String OBJ_WIDTH = "width";
    static final String OBJ_HEIGHT = "height";
    static final String OBJ_ACTION = "action";

    //action element
    public static final String ACTION_TYPE = "type";
    public static final String ACTION_URL = "url";
    public static final String ACTION_ACTIONINFO = "actioninfo";

    // actioninfo element
    public static final String ACTIONINFO_OS = "os";
    public static final String ACTIONINFO_DEVICETYPE = "devicetype";
    public static final String ACTIONINFO_EXEC_PARAM = "execparam";
}
