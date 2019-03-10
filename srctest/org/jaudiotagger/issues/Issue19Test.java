package org.jaudiotagger.issues;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.field.Mp4DiscNoField;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class Issue19Test {

    @Test
    public void mp4() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("testIssue19.m4a");
        AudioFile f = AudioFileIO.read(testFile);
        Tag tag = f.getTag();

        //Change values to different value (but same no of characters, this is the easiest mod to make
        tag.setField(FieldKey.ARTIST, "AUTHOR");
        tag.setField(FieldKey.ALBUM, "ALBUM");
        //tag.setField(FieldKey.TRACK,"2");
        tag.setField(tag.createField(FieldKey.TRACK, "2"));
        tag.setField(tag.createField(FieldKey.TRACK_TOTAL, "12"));
        Assert.assertEquals("2", tag.getFirst(FieldKey.TRACK));
        //tag.setField(tag.createField(FieldKey.DISC_NO,"4/15"));
        tag.setField(new Mp4DiscNoField(4, 15));
        tag.setField(tag.createField(FieldKey.MUSICBRAINZ_TRACK_ID, "e785f700-c1aa-4943-bcee-87dd316a2c31"));
        tag.setField(tag.createField(FieldKey.BPM, "300"));
        //Save changes and reread from disk
        f.commit();
        f = AudioFileIO.read(testFile);
        tag = f.getTag();
    }

}
