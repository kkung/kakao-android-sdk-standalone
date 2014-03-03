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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author kkung
 */
public class FilePart implements BodyPart {
    private final String key;
    private final File file;

    public FilePart(String key, File file) {

        this.key = key;
        this.file = file;
    }

    @Override
    public String getPartHeaderString() {
        return new StringBuilder("Content-Disposition: form-data; name=\"")
                .append(key).append("\"; filename=\"").append(key).append("\"")
                .toString();
    }

    @Override
    public byte[] getBody(String encoding) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupportCopy() {
        return true;
    }

    @Override
    public void copy(OutputStream out) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] buf = new byte[8192];
            while(true) {
                int readlen = in.read(buf);
                if (readlen < 1)
                    break;
                out.write(buf, 0, readlen);
            }
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }
}
