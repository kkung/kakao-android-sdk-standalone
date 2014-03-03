package com.kakao.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kkung
 */
public class Multipart {

    private static final String boundary = "comkakakoandroidsdk";
    private String encoding;
    private List<BodyPart> parts;

    public Multipart(final String encoding) {
        this.encoding = encoding;
        this.parts = new ArrayList<BodyPart>();
    }

    public String getContentType() {
        StringBuilder sb = new StringBuilder("multipart/form-data; ")
                .append("boundary=").append(boundary).append("; ")
                .append("charset=\"").append(encoding).append("\"");
        return sb.toString();
    }

    public void writeTo(OutputStream os) throws IOException {

        String mpStart = "--" + boundary + "\r\n";
        os.write(mpStart.getBytes(encoding));

        for (BodyPart part : parts) {
            os.write((part.getPartHeaderString() + "\r\n\r\n").getBytes(encoding));
            if (part.isSupportCopy()) {
                part.copy(os);
            } else {
                os.write(part.getBody(encoding));
            }
        }

        String mpEnd = "\r\n--" + boundary + "--\r\n";
        os.write(mpEnd.getBytes(encoding));
    }

    public void addBodyPart(BodyPart part) {
        this.parts.add(part);
    }
}
