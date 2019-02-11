package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * @author The JCodec project
 * 
 */
public class MdtaBox extends Box {

    private static final String FOURCC = "mdta";
    private String key;

    public MdtaBox(Header header) {
        super(header);
    }

    public static MdtaBox createMdtaBox(String key) {
        MdtaBox box = new MdtaBox(Header.createHeader(FOURCC, 0));
        box.key = key;
        return box;
    }

    @Override
    public void parse(ByteBuffer buf) {
        key = new String(Utils.toArray(Utils.readBuf(buf)), Charset.forName(TextEncoding.CHARSET_US_ASCII));
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void doWrite(ByteBuffer out) {
        out.put(key.getBytes());
    }

    @Override
    public int estimateSize() {
        return key.getBytes(Charset.forName(TextEncoding.CHARSET_US_ASCII)).length;
    }
    
    public static String fourcc() {
        return FOURCC;
    }
}
