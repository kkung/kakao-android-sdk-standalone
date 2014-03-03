package com.kakao.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author kkung
 */
public interface BodyPart {
    String getPartHeaderString();
    byte[] getBody(String encoding) throws IOException;

    boolean isSupportCopy();

    void copy(OutputStream os) throws IOException;
}
