package org.jcodec.containers.mp4.boxes;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * @author The JCodec project
 * 
 */
public class UrlBox extends FullBox {

    private String url;

    public static String fourcc() {
        return "url ";
    }

    public static UrlBox createUrlBox(String url) {
        UrlBox urlBox = new UrlBox(new Header(fourcc()));
        urlBox.url = url;
        return urlBox;
    }

    public UrlBox(Header atom) {
        super(atom);
    }

    @Override
    public void parse(ByteBuffer input) {
        super.parse(input);
        if ((flags & 0x1) != 0)
            return;
        url = Utils.readNullTermStringCharset(input, Charset.forName(TextEncoding.CHARSET_UTF_8));
    }

    @Override
    protected void doWrite(ByteBuffer out) {
        super.doWrite(out);

        if (url != null) {
            Utils.write(out, ByteBuffer.wrap(url.getBytes(Charset.forName(TextEncoding.CHARSET_UTF_8))));
            out.put((byte) 0);
        }
    }
    
    @Override
    public int estimateSize() {
        int sz = 13;

        if (url != null) {
            sz += url.getBytes(Charset.forName(TextEncoding.CHARSET_UTF_8)).length;
        }
        return sz;
    }

    public String getUrl() {
        return url;
    }
}
