package org.jaudiotagger.audio.mp4;

import org.jaudiotagger.audio.generic.Utils;
import org.jcodec.containers.mp4.boxes.*;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 *
 * @author The JCodec project
 */
public class ChunkWriter {
    private long[] offsets;
    private SampleEntry[] entries;
    private FileChannel[] inputs;
    private int curChunk;
    private FileChannel out;
    byte[] buf;
    private TrakBox trak;

    public ChunkWriter(TrakBox trak, FileChannel[] inputs, FileChannel out) {
        this.buf = new byte[8092];
        entries = trak.getSampleEntries();
        ChunkOffsetsBox stco = trak.getStco();
        ChunkOffsets64Box co64 = trak.getCo64();
        int size;
        if (stco != null)
            size = stco.getChunkOffsets().length;
        else
            size = co64.getChunkOffsets().length;
        this.inputs = inputs;

        offsets = new long[size];
        this.out = out;
        this.trak = trak;
    }

    public void apply() {
        NodeBox stbl = NodeBox.findFirstPath(trak, NodeBox.class, Box.path("mdia.minf.stbl"));
        stbl.removeChildren(new String[]{"stco", "co64"});

        stbl.add(ChunkOffsets64Box.createChunkOffsets64Box(offsets));
        cleanDrefs(trak);
    }

    private void cleanDrefs(TrakBox trak) {
        MediaInfoBox minf = trak.getMdia().getMinf();
        DataInfoBox dinf = trak.getMdia().getMinf().getDinf();
        if (dinf == null) {
            dinf = DataInfoBox.createDataInfoBox();
            minf.add(dinf);
        }

        DataRefBox dref = dinf.getDref();
        if (dref == null) {
            dref = DataRefBox.createDataRefBox();
            dinf.add(dref);
        }

        dref.getBoxes().clear();
        dref.add(AliasBox.createSelfRef());

        SampleEntry[] sampleEntries = trak.getSampleEntries();
        for (int i = 0; i < sampleEntries.length; i++) {
            SampleEntry entry = sampleEntries[i];
            entry.setDrefInd((short) 1);
        }
    }

    private FileChannel getInput(Chunk chunk) {
        SampleEntry se = entries[chunk.getEntry() - 1];
        return inputs[se.getDrefInd() - 1];
    }

    public void write(Chunk chunk) throws IOException {
        FileChannel input = getInput(chunk);
        input.position(chunk.getOffset());
        long pos = out.position();

        out.write(Utils.fetchFromChannel(input, (int) chunk.getSize()));
        offsets[curChunk++] = pos;
    }
}