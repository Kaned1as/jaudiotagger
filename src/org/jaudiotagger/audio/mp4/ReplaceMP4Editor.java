package org.jaudiotagger.audio.mp4;

import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.MP4Util.Movie;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.MovieBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A full fledged MP4 editor.
 * 
 * Parses MP4 file, applies the edit and saves the result in a new file.
 * 
 * Unlike InplaceMP4Edit any changes are allowed. This class will take care of
 * adjusting all the sample offsets so the result file will be correct.
 * 
 * @author The JCodec project
 * 
 */
public class ReplaceMP4Editor {

    public void modifyOrReplace(File src, MovieBox edit) throws IOException {
        boolean modify = new InplaceMP4Editor().modify(new FileInputStream(src).getChannel(), edit);
        if (!modify)
            replace(src, edit);
    }

    public void replace(File src, MovieBox edit) throws IOException {
        File tmp = new File(src.getParentFile(), "." + src.getName());
        copy(src, tmp, edit);
        tmp.renameTo(src);
    }

    public void copy(File src, File dst, MovieBox edit) throws IOException {
        final Movie movie = MP4Util.createRefFullMovieFromFile(src);

        for (Box box: edit.getBoxes()) {
            movie.getMoov().replaceBox(box);
        }

        Flattern fl = new Flattern();
        fl.flattern(movie, dst);
    }
}