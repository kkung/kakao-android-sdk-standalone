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
