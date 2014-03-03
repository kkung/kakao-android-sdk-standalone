package com.kakao.http;

import java.util.Map;

/**
 * @author kkung
 */
public interface Request {
    String getUrl();
    Map<String, String> getHeaders();
    String getMethod();
    boolean hasPayload();
    String getCharSet();

    Object getPayload();
}
