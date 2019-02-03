package org.jaudiotagger.issues;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp4.Mp4AtomTree;
import org.jcodec.containers.mp4.MP4Util;
import org.json.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Test writing mp4
 */
public class Issue198Test extends AbstractTestCase {

    public void testIssue() {
        Exception caught = null;
        try {
            File orig = new File("testdata", "issue-198.m4a");
            if (!orig.isFile()) {
                System.err.println("Unable to test file - not available");
                return;
            }

            File testFile = AbstractTestCase.copyAudioToTmp("issue-198.m4a");
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getAudioHeader());
            af.getTagOrCreateAndSetDefault();
            af.commit();

            MP4Util.Movie mp4 = MP4Util.parseFullMovie(testFile);
            String json = new JSONObject(mp4.getMoov().toString()).toString(2);
            System.out.println(json);

        } catch (Exception e) {
            caught = e;
            e.printStackTrace();
        }
        assertNull(caught);
    }
}