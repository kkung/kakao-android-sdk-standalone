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
