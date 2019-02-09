package org.jaudiotagger.audio.mp4;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.datatype.Pair;
import org.jcodec.containers.mp4.BoxFactory;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.MP4Util.Atom;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;

import java.io.*;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * Parses MP4 header and allows custom MP4Editor to modify it, then tries to put
 * the resulting header into the same place relatively to a file.
 * 
 * This might not work out, for example if the resulting header is bigger then
 * the original.
 * 
 * Use this class to make blazing fast changes to MP4 files when you know your
 * are not adding anything new to the header, perhaps only patching some values
 * or removing stuff from the header.
 * 
 * @author The JCodec project
 * 
 */
public class InplaceMP4Editor {

    /**
     * Tries to modify movie header in place according to what's implemented in
     * the edit, the file gets physically modified if the operation is
     * successful. No temporary file is created.
     * 
     * @param fi A file channel to be modified
     * @param edit
     *            An edit to be carried out on a movie header
     * @return Whether or not edit was successful, i.e. was there enough place
     *         to put the new header
     * @throws IOException
     * @throws Exception
     */
    public boolean modify(FileChannel fi, MovieBox edit) throws IOException {
        Atom moovAtom = getMoov(fi);

        ByteBuffer moovBuffer = fetchBox(fi, moovAtom);
        MovieBox moovBox = (MovieBox) parseBox(moovBuffer);

        for (Box box: edit.getBoxes()) {
            moovBox.replaceBox(box);
        }

        return rewriteBox(moovBuffer, moovBox);
    }

    private boolean rewriteBox(ByteBuffer buffer, Box box) {
        try {
            buffer.clear();
            box.write(buffer);
            if (buffer.hasRemaining()) {
                if (buffer.remaining() < 8)
                    return false;
                buffer.putInt(buffer.remaining());
                buffer.put(new byte[] { 'f', 'r', 'e', 'e' });
            }
            buffer.flip();
            return true;
        } catch (BufferOverflowException e) {
            return false;
        }
    }

    private ByteBuffer fetchBox(FileChannel fi, Atom moov) throws IOException {
        fi.position(moov.getOffset());
        ByteBuffer oldMov = Utils.fetchFromChannel(fi, (int) moov.getHeader().getSize());
        return oldMov;
    }

    private Box parseBox(ByteBuffer oldMov) {
        Header header = Header.read(oldMov);
        Box box = Box.parseBox(oldMov, header, BoxFactory.getDefault());
        return box;
    }

    private Atom getMoov(FileChannel f) throws IOException {
        for (Atom atom : MP4Util.getRootAtoms(f)) {
            if ("moov".equals(atom.getHeader().getFourcc())) {
                return atom;
            }
        }
        return null;
    }
}
