/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
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
package com.kakao.http;

import java.util.Map;

/**
 * @author kkung
 */
public interface Request {

    /**
     * @return 요청 주소
     */
    String getUrl();

    /**
     * @return 요청에 포함할 Header
     */
    Map<String, String> getHeaders();

    /**
     * @return 요청의 HTTP Verb
     */
    String getMethod();

    /**
     * @return POST할 데이터가 있는지 여부
     */
    boolean hasPayload();

    /**
     * @return 요청시 사용할 {@link java.nio.charset.Charset}의 이름
     */
    String getCharSet();

    /**
     * @return POST할 내용
     */
    Object getPayload();
}
