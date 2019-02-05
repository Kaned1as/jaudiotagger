package org.jaudiotagger.tag.mp4.field;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.mp4.atom.Mp4BoxHeader;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.mp4.Mp4TagField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Represents raw binary data
 *
 * <p>We use this when we find an atom under the ilst atom that we do not recognise , that does not
 * follow standard conventions in order to save the data without modification so it can be safetly
 * written back to file
 */
public class Mp4TagRawBinaryField extends Mp4TagField {
    protected int dataSize;
    protected byte[] dataBytes;

    protected Mp4TagRawBinaryField(String id) {
        super(id);
    }


    public Mp4FieldType getFieldType() {
        return Mp4FieldType.IMPLICIT;
    }

    /**
     * Used when creating raw content
     *
     * @return
     */
    protected byte[] getDataBytes() {
        return dataBytes;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isEmpty() {
        return this.dataBytes.length == 0;
    }

    public int getDataSize() {
        return dataSize;

    }

    public byte[] getData() {
        return this.dataBytes;
    }

    public void setData(byte[] d) {
        this.dataBytes = d;
    }

    public void copyContent(TagField field) {
        throw new UnsupportedOperationException("not done");
    }

    public byte[] getRawContent() throws UnsupportedEncodingException {
        logger.fine("Getting Raw data for:" + getId());
        try {
            ByteArrayOutputStream outerbaos = new ByteArrayOutputStream();
            outerbaos.write(Utils.getSizeBEInt32(Mp4BoxHeader.HEADER_LENGTH + dataSize));
            outerbaos.write(getId().getBytes(Charset.forName("ISO-8859-1")));
            outerbaos.write(dataBytes);
            return outerbaos.toByteArray();
        } catch (IOException ioe) {
            //This should never happen as were not actually writing to/from a file
            throw new RuntimeException(ioe);
        }
    }

}
