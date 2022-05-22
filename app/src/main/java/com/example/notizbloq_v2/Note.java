package com.example.notizbloq_v2;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable {
    // Bei Änderungen der Klasse muss die serialVersionUID übereinstimmen, damit die Serialisierung funktioniert.
    private long createdDtTm, modifiedDtTm;
    private String noteTitle, noteText, imageUrl, audioUrl;
    private Integer thumbnailImageId = null;

    public Note(long createdDtTm, long modifiedDtTm, String title, String text) {
        this.createdDtTm = createdDtTm;
        this.modifiedDtTm = modifiedDtTm;
        this.noteTitle = title;
        this.noteText = text;
    }

    public Note(long createdDtTm, long modifiedDtTm, String title, String text, String image, String audio) {
        this.createdDtTm = createdDtTm;
        this.modifiedDtTm = modifiedDtTm;
        this.noteTitle = title;
        this.noteText = text;
        this.imageUrl = image;
        this.audioUrl = audio;
    }

    public void setNoteTitle(String newTitle) {
        this.noteTitle = newTitle;
    }

    public void setNoteText(String newText) {
        this.noteText = newText;
    }

    public void setThumbnailImageId(int newImageId) {
        this.thumbnailImageId = newImageId;
    }

    public void setModifiedDtTm(long modifiedDtTm) { this.modifiedDtTm = modifiedDtTm; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getNoteText() { return this.noteText; }

    public String getNoteTitle() { return this.noteTitle; }

    public Integer getThumbnailImageId() { return thumbnailImageId; }

    public long getCreatedDtTm() { return createdDtTm; }

    public String getFileName() { return createdDtTm + Utilities.FILE_EXTENSION; }

    public String getImageUrl() { return imageUrl; }

    public String getAudioUrl() { return audioUrl; }

    // Zum Formatieren der Zeitstempel
    public static String getDtTmFormatted(Context context, long dttm) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(dttm));
    }


}
