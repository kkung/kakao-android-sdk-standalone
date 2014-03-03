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
package com.kakao;

/**
 * 스토리 포스팅을 위한 이미지 업로드 요청 결과 객체로
 * 업로드된 이미지의 URL을 포함한다.
 * @author MJ
 */
public class KakaoStoryUpload {
    private String url;

    // for jackson
    public KakaoStoryUpload(final String url) {
        this.url = url;
    }

    /**
     * 업로드된 이미지의 URL
     * @return 업로드된 이미지의 URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 각 정보를 string으로 표현하여 반환한다.
     * @return 각 정보를 포함한 string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StoryUpload{");
        sb.append("url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
