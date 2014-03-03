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

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author kkung
 */
public interface BodyPart {

    /**
     * @return Part에 포함될 Header
     */
    String getPartHeaderString();

    /**
     * Part의 인코딩된 결과를 반환합니다.
     * @param encoding Part를 인코딩할 {@link java.nio.charset.Charset}의 이름
     * @return Part 내용
     * @throws IOException
     */
    byte[] getBody(String encoding) throws IOException;

    /**
     * @return {@link com.kakao.http.BodyPart#copy(java.io.OutputStream)}를 지원하는지 여부
     */
    boolean isSupportCopy();

    /**
     * {@link com.kakao.http.BodyPart#getBody(String)})와는 다르게
     * 직접 {@link java.io.OutputStream}으로 내용을 전송합니다.
     * @param os Part의 내용이 기록될 {@link java.io.OutputStream}
     * @throws IOException
     */
    void copy(OutputStream os) throws IOException;
}
